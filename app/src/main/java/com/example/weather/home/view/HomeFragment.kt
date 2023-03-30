package com.example.weather.home.view

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
import com.example.weather.database.room.RoomStatus
import com.example.weather.database.room.entity.EntityHome
import com.example.weather.home.model.GPSLocation
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.home.model.LocationStatus
import com.example.weather.home.viewmodel.HomeViewModel
import com.example.weather.home.viewmodel.HomeViewModelFactory
import com.example.weather.model.repository.Repository
import com.example.weather.network.APIClient
import com.example.weather.network.APIState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var dayAdapter:DayAdapter
    private lateinit var hourAdapter:HourAdapter

    private lateinit var entityHome: EntityHome

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(APIClient.getInstance(),
                ConceretLocalSource(requireContext())),
            GPSLocation.getInstance(requireContext()),
            SharedPreferenceSource.getInstance(requireContext())
        )

        homeViewModel = ViewModelProvider(this,homeViewModelFactory)[HomeViewModel::class.java]

        if(checkForInternet(requireContext())) {
            lifecycleScope.launch {
                GPSLocation.getInstance(requireContext()).location.collectLatest {
                    when (it) {
                        is LocationStatus.Success -> {
                            binding.locationText.text = getLocationFromLatAndLon(
                                it.location.latitude, it.location.longitude
                            )

                            entityHome = EntityHome()
                            entityHome.lat = it.location.latitude
                            entityHome.lon = it.location.longitude
                        }
                        is LocationStatus.Failure -> {

                        }
                        else -> {

                        }
                    }
                }
            }
            lifecycleScope.launch {
                homeViewModel.weather.collectLatest {
                    when (it) {
                        is APIState.Loading -> {
                            loading()
                        }
                        is APIState.Success -> {
                            success()

                            entityHome.current = it.weather.current
                            entityHome.hourly = it.weather.hourly
                            entityHome.daily = it.weather.daily
                            entityHome.current!!.weather[0].icon = "https://openweathermap.org/img/wn/"+
                                    it.weather.current!!.weather[0].icon + "@2x.png"

                            for (i in 0..47){
                                entityHome.hourly[i].weather[0].icon = "https://openweathermap.org/img/wn/"+
                                        it.weather.hourly[i].weather[0].icon + "@2x.png"
                            }

                            for (i in 0..7){
                                entityHome.daily[i].weather[0].icon = "https://openweathermap.org/img/wn/"+
                                        it.weather.daily[i].weather[0].icon + "@2x.png"
                            }

                            homeViewModel.insertHomeWeather(entityHome)

                            binding.hour.text = getHourFromTimestamp(it.weather.current.dt)
                            binding.dateText.text = getDateFromTimestamp(it.weather.current.dt)

                            binding.description.text = it.weather.current.weather[0].description
                            binding.status.text = it.weather.current.weather[0].main
                            binding.temperature.text = it.weather.current.temp.toString()
                            Glide.with(requireContext())
                                .load(
                                            it.weather.current.weather[0].icon
                                )
                                .apply(
                                    RequestOptions().override(
                                        binding.image.width,
                                        binding.image.height
                                    )
                                ).into(binding.image)

                            binding.textValuePressure.text = it.weather.current.pressure.toString()
                            binding.textValueHumidity.text = it.weather.current.humidity.toString()
                            binding.textValueCloud.text = it.weather.current.clouds.toString()
                            binding.textValueSunRise.text =
                                getHourFromTimestamp(it.weather.current.sunrise)
                            binding.textValueVisibility.text =
                                it.weather.current.visibility.toString()
                            binding.textValueWindSpeed.text =
                                it.weather.current.wind_speed.toString()

                            hourAdapter = HourAdapter(requireContext())
                            binding.recyclerToday.apply {
                                adapter = hourAdapter
                                hourAdapter.submitList(it.weather.hourly)
                                layoutManager = LinearLayoutManager(context).apply {
                                    orientation = RecyclerView.HORIZONTAL
                                }
                            }

                            dayAdapter = DayAdapter(requireContext())
                            binding.recyclerWeekly.apply {
                                adapter = dayAdapter
                                dayAdapter.submitList(it.weather.daily)
                                layoutManager = LinearLayoutManager(context).apply {
                                    orientation = RecyclerView.VERTICAL
                                }
                            }
                        }
                        else -> {

                        }
                    }
                }
            }
        }else{
            lifecycleScope.launch {
                homeViewModel.room.collectLatest {
                    when (it) {
                        is RoomStatus.Loading -> {
                            loading()
                        }
                        is RoomStatus.Success -> {
                            success()
                            binding.hour.text = getHourFromTimestamp(it.weather.current!!.dt)
                            binding.dateText.text = getDateFromTimestamp(it.weather.current!!.dt)
                            binding.locationText.text = getLocationFromLatAndLon(it.weather.lat,
                                it.weather.lon)

                            binding.description.text = it.weather.current!!.weather[0].description
                            binding.status.text = it.weather.current!!.weather[0].main
                            binding.temperature.text = it.weather.current!!.temp.toString()
                            Glide.with(requireContext())
                                .load(
                                    it.weather.current!!.weather[0].icon
                                )
                                .apply(
                                    RequestOptions().override(
                                        binding.image.width,
                                        binding.image.height
                                    )
                                ).into(binding.image)

                            binding.textValuePressure.text = it.weather.current!!.pressure.toString()
                            binding.textValueHumidity.text = it.weather.current!!.humidity.toString()
                            binding.textValueCloud.text = it.weather.current!!.clouds.toString()
                            binding.textValueSunRise.text =
                                getHourFromTimestamp(it.weather.current!!.sunrise)
                            binding.textValueVisibility.text =
                                it.weather.current!!.visibility.toString()
                            binding.textValueWindSpeed.text =
                                it.weather.current!!.wind_speed.toString()

                            hourAdapter = HourAdapter(requireContext())
                            binding.recyclerToday.apply {
                                adapter = hourAdapter
                                hourAdapter.submitList(it.weather.hourly)
                                layoutManager = LinearLayoutManager(context).apply {
                                    orientation = RecyclerView.HORIZONTAL
                                }
                            }

                            dayAdapter = DayAdapter(requireContext())
                            binding.recyclerWeekly.apply {
                                adapter = dayAdapter
                                dayAdapter.submitList(it.weather.daily)
                                layoutManager = LinearLayoutManager(context).apply {
                                    orientation = RecyclerView.VERTICAL
                                }
                            }
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }
    private fun loading(){
        binding.animationLoading.visibility = View.VISIBLE
        binding.cardDetails.visibility = View.GONE
        binding.description.visibility = View.GONE
        binding.status.visibility = View.GONE
        binding.dateText.visibility = View.GONE
        binding.hour.visibility = View.GONE
        binding.image.visibility = View.GONE
        binding.temperature.visibility = View.GONE
        binding.locationText.visibility = View.GONE
    }
    private fun success(){
        binding.animationLoading.visibility = View.GONE
        binding.cardDetails.visibility = View.VISIBLE
        binding.description.visibility = View.VISIBLE
        binding.status.visibility = View.VISIBLE
        binding.dateText.visibility = View.VISIBLE
        binding.hour.visibility = View.VISIBLE
        binding.image.visibility = View.VISIBLE
        binding.temperature.visibility = View.VISIBLE
        binding.locationText.visibility = View.VISIBLE
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
        return address[0].subAdminArea
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