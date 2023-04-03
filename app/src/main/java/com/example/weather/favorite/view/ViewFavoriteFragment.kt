package com.example.weather.favorite.view

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weather.database.room.ConceretLocalSource
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.databinding.FragmentViewFavoriteBinding
import com.example.weather.favorite.viewmodel.FavoriteViewModel
import com.example.weather.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weather.home.view.DayAdapter
import com.example.weather.home.view.HourAdapter
import com.example.weather.model.repository.Repository
import com.example.weather.network.APIClient
import com.example.weather.network.APIState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ViewFavoriteFragment : Fragment() {

    private lateinit var binding : FragmentViewFavoriteBinding
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    private lateinit var dayAdapter:DayAdapter
    private lateinit var hourAdapter:HourAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentViewFavoriteBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item : EntityFavorite = arguments?.getParcelable("EntityFavorite")!!

        favoriteViewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                APIClient.getInstance(),
                ConceretLocalSource(requireContext())
            )
        )

        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory)[FavoriteViewModel::class.java]

        if (checkForInternet(requireContext())) {

            favoriteViewModel.getWeather(
                item.lat,
                item.lon,
                SharedPreferenceSource.getInstance(requireContext())
                    .getSavedUnit(),
                SharedPreferenceSource.getInstance(requireContext())
                    .getSavedLanguage()
            )

            lifecycleScope.launch {
                favoriteViewModel.weather.collectLatest {
                    when (it) {
                        is APIState.Loading -> {
                            loading()
                        }
                        is APIState.Success -> {
                            success()
                            binding.hourFavorite.text =
                                getHourFromTimestamp(it.weather.current.dt)
                            binding.dateTextFavorite.text =
                                getDateFromTimestamp(it.weather.current.dt)
                            binding.locationTextFavorite.text =
                                getLocationFromLatAndLon(
                                    it.weather.lat, it.weather.lon
                                )

                            binding.descriptionFavorite.text =
                                it.weather.current.weather[0].description
                            binding.statusFavorite.text =
                                it.weather.current.weather[0].main
                            binding.temperatureFavorite.text =
                                it.weather.current.temp.toString()
                            Glide.with(requireContext())
                                .load(
                                    "https://openweathermap.org/img/wn/" +
                                            it.weather.current.weather[0].icon + "@2x.png"
                                )
                                .apply(
                                    RequestOptions().override(
                                        binding.imageFavorite.width,
                                        binding.imageFavorite.height
                                    )
                                ).into(binding.imageFavorite)

                            binding.textValuePressureFavorite.text =
                                it.weather.current.pressure.toString()
                            binding.textValueHumidityFavorite.text =
                                it.weather.current.humidity.toString()
                            binding.textValueCloudFavorite.text =
                                it.weather.current.clouds.toString()
                            binding.textValueSunRiseFavorite.text =
                                getHourFromTimestamp(it.weather.current.sunrise)
                            binding.textValueVisibilityFavorite.text =
                                it.weather.current.visibility.toString()
                            binding.textValueWindSpeedFavorite.text =
                                it.weather.current.wind_speed.toString()

                            hourAdapter = HourAdapter(requireContext())
                            binding.recyclerTodayFavorite.apply {
                                adapter = hourAdapter
                                hourAdapter.submitList(it.weather.hourly)
                                layoutManager = LinearLayoutManager(context).apply {
                                    orientation = RecyclerView.HORIZONTAL
                                }
                            }

                            dayAdapter = DayAdapter(requireContext())
                            binding.recyclerWeeklyFavorite.apply {
                                adapter = dayAdapter
                                dayAdapter.submitList(it.weather.daily)
                                layoutManager = LinearLayoutManager(context).apply {
                                    orientation = RecyclerView.VERTICAL
                                }
                            }
                        }
                        else -> {
                            Snackbar.make(binding.root,"Please, Check your connection", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }else{
            success()
            binding.hourFavorite.text =
                getHourFromTimestamp(item.current!!.dt)
            binding.dateTextFavorite.text =
                getDateFromTimestamp(item.current!!.dt)
            binding.locationTextFavorite.text =
                getLocationFromLatAndLon(
                    item.lat, item.lon
                )

            binding.descriptionFavorite.text =
                item.current!!.weather[0].description
            binding.statusFavorite.text =
                item.current!!.weather[0].main
            binding.temperatureFavorite.text =
                item.current!!.temp.toString()
            Glide.with(requireContext())
                .load(
                    "https://openweathermap.org/img/wn/" +
                            item.current!!.weather[0].icon + "@2x.png"
                )
                .apply(
                    RequestOptions().override(
                        binding.imageFavorite.width,
                        binding.imageFavorite.height
                    )
                ).into(binding.imageFavorite)

            binding.textValuePressureFavorite.text =
                item.current!!.pressure.toString()
            binding.textValueHumidityFavorite.text =
                item.current!!.humidity.toString()
            binding.textValueCloudFavorite.text =
                item.current!!.clouds.toString()
            binding.textValueSunRiseFavorite.text =
                getHourFromTimestamp(item.current!!.sunrise)
            binding.textValueVisibilityFavorite.text =
                item.current!!.visibility.toString()
            binding.textValueWindSpeedFavorite.text =
                item.current!!.wind_speed.toString()

            hourAdapter = HourAdapter(requireContext())
            binding.recyclerTodayFavorite.apply {
                adapter = hourAdapter
                hourAdapter.submitList(item.hourly)
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = RecyclerView.HORIZONTAL
                }
            }

            dayAdapter = DayAdapter(requireContext())
            binding.recyclerWeeklyFavorite.apply {
                adapter = dayAdapter
                dayAdapter.submitList(item.daily)
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = RecyclerView.VERTICAL
                }
            }
        }
    }
    private fun loading(){
        binding.animationLoading.visibility = View.VISIBLE
        binding.cardDetailsFavorite.visibility = View.GONE
        binding.descriptionFavorite.visibility = View.GONE
        binding.statusFavorite.visibility = View.GONE
        binding.dateTextFavorite.visibility = View.GONE
        binding.hourFavorite.visibility = View.GONE
        binding.imageFavorite.visibility = View.GONE
        binding.temperatureFavorite.visibility = View.GONE
        binding.locationTextFavorite.visibility = View.GONE
    }
    private fun success(){
        binding.animationLoading.visibility = View.GONE
        binding.cardDetailsFavorite.visibility = View.VISIBLE
        binding.descriptionFavorite.visibility = View.VISIBLE
        binding.statusFavorite.visibility = View.VISIBLE
        binding.dateTextFavorite.visibility = View.VISIBLE
        binding.hourFavorite.visibility = View.VISIBLE
        binding.imageFavorite.visibility = View.VISIBLE
        binding.temperatureFavorite.visibility = View.VISIBLE
        binding.locationTextFavorite.visibility = View.VISIBLE
    }
    @SuppressLint("SimpleDateFormat")
    private fun getHourFromTimestamp(timestamp: Long): String {
        val time = Date(timestamp * 1000)
        val simpleDateFormat = SimpleDateFormat("h:mm aaa")
        return simpleDateFormat.format(time)
    }
    @SuppressLint("SimpleDateFormat")
    private fun getDateFromTimestamp(timestamp: Long):String{
        val time = Date(timestamp * 1000)
        val simpleDateFormat = SimpleDateFormat("EEEE, dd LLL")
        return simpleDateFormat.format(time)
    }

    private fun getLocationFromLatAndLon(lat: Double, lon: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geocoder.getFromLocation(lat, lon, 1) as List<Address>
        return if (address.isNotEmpty()) {
            if (address[0].locality == null){
                "null"
            }else{
                address[0].locality
            }
        }else
            "null"
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

