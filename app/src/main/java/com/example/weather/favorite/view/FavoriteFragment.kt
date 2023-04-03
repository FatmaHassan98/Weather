package com.example.weather.favorite.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.database.room.ConceretLocalSource
import com.example.weather.database.room.FavoriteStatus
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.databinding.FragmentFavoriteBinding
import com.example.weather.favorite.viewmodel.FavoriteViewModel
import com.example.weather.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weather.map.view.MapsActivity
import com.example.weather.model.repository.Repository
import com.example.weather.network.APIClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() , FavoriteClickLisener {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.btnAdd.setOnClickListener {
            if (checkForInternet(requireContext())) {
                SharedPreferenceSource.getInstance(requireContext()).setMap("favorite")
                val intent = Intent(requireActivity(), MapsActivity::class.java)
                requireActivity().startActivity(intent)
            }else{
                Snackbar.make(binding.root,"Please, Check your connection",Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        favoriteViewModelFactory = FavoriteViewModelFactory(Repository.getInstance(
            APIClient.getInstance(),
            ConceretLocalSource(requireContext()))
        )

        favoriteViewModel = ViewModelProvider(this,favoriteViewModelFactory)[FavoriteViewModel::class.java]


        lifecycleScope.launch {
            favoriteViewModel.favorite.collectLatest {
                when(it){
                    is FavoriteStatus.Loading ->{
                        binding.recyclerFavorite.visibility = View.GONE
                        binding.animationFavorite.visibility = View.VISIBLE
                    }
                    is FavoriteStatus.Success ->{
                        if (it.entityFavorite.isEmpty()) {
                            binding.recyclerFavorite.visibility = View.GONE
                            binding.animationFavorite.visibility = View.VISIBLE
                        }else {
                            binding.recyclerFavorite.visibility = View.VISIBLE
                            binding.animationFavorite.visibility = View.GONE

                            favoriteAdapter =
                                FavoriteAdapter(requireContext(), this@FavoriteFragment)
                            binding.recyclerFavorite.apply {
                                adapter = favoriteAdapter
                                favoriteAdapter.submitList(it.entityFavorite)
                                layoutManager = LinearLayoutManager(context).apply {
                                    orientation = RecyclerView.VERTICAL
                                }
                            }
                        }
                    }else ->{
                        Snackbar.make(binding.root,"Please, Check your connection",Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun deleteFavorite(entityFavorite: EntityFavorite) {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setMessage("Are you sure!\nYou want to delete this item ?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes"){ dialog: DialogInterface, which: Int ->
            favoriteViewModel.deleteFavorite(entityFavorite)
            dialog.cancel()
        }
        builder.setNegativeButton("No"){ dialog: DialogInterface, which: Int ->
            dialog.cancel()
        }
        val alertDialog: android.app.AlertDialog? = builder.create()
        alertDialog?.show()
    }

    override fun showData(entityFavorite: EntityFavorite) {
        val action = FavoriteFragmentDirections.actionNavigationFavoriteToViewFavoriteFragment(entityFavorite)
        binding.root.findNavController().navigate(action)
    }

    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork ?: return false

            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

}