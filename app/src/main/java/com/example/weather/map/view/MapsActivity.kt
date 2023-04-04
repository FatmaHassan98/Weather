package com.example.weather.map.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weather.R
import com.example.weather.database.room.ConceretLocalSource
import com.example.weather.database.room.WeatherDao
import com.example.weather.database.room.WeatherDatabase
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.databinding.ActivityMapsBinding
import com.example.weather.home.model.GPSLocation
import com.example.weather.home.model.LocationStatus
import com.example.weather.home.viewmodel.HomeViewModel
import com.example.weather.home.viewmodel.HomeViewModelFactory
import com.example.weather.map.viewmodel.MapViewModel
import com.example.weather.map.viewmodel.MapViewModelFactory
import com.example.weather.model.repository.Repository
import com.example.weather.network.APIClient
import com.example.weather.network.APIState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraIdleListener,
    GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val REQUEST_CHECK_SETTINGS: Int=101
    private var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapViewModelFactory: MapViewModelFactory
    private lateinit var lastLocation: Location
    private lateinit var locationRequest: LocationRequest
    private var lat = 0.0
    private var lon = 0.0
    private lateinit var entityFavorite: EntityFavorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val weatherDao : WeatherDao by lazy {
            val appDataBase: WeatherDatabase = WeatherDatabase.getInstance(this@MapsActivity)
            appDataBase.getHomeWeather()
        }

        mapViewModelFactory = MapViewModelFactory(
            Repository.getInstance(
                APIClient.getInstance(),
                ConceretLocalSource(weatherDao)
            ),
            GPSLocation.getInstance(this),
            SharedPreferenceSource.getInstance(this)
        )


        mapViewModel = ViewModelProvider(this,mapViewModelFactory)[MapViewModel::class.java]

        binding.btnSaveMap.setOnClickListener {

            if(checkForInternet(this)) {
                Snackbar.make(
                    binding.root, "Your data is saved",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                if (SharedPreferenceSource.getInstance(this).getSavedMap() == "favorite") {

                    SharedPreferenceSource.getInstance(this).setLatAndLon(lat, lon)

                    lifecycleScope.launch {

                        mapViewModel.getWeather(
                            SharedPreferenceSource.getInstance(this@MapsActivity).getLat(),
                            SharedPreferenceSource.getInstance(this@MapsActivity).getLon(),
                            SharedPreferenceSource.getInstance(this@MapsActivity)
                                .getSavedUnit(),
                            SharedPreferenceSource.getInstance(this@MapsActivity).getSavedLanguage()
                        )

                        mapViewModel.weather.collectLatest {
                            when (it) {
                                is APIState.Success -> {
                                    entityFavorite = EntityFavorite()
                                    entityFavorite.lat = lat
                                    entityFavorite.lon = lon
                                    entityFavorite.current = it.weather.current
                                    entityFavorite.hourly = it.weather.hourly
                                    entityFavorite.daily = it.weather.daily
                                    entityFavorite.current!!.weather[0].icon =
                                        it.weather.current.weather[0].icon

                                    mapViewModel.insertFavorite(entityFavorite)
                                }
                                is APIState.Failure -> {
                                    Snackbar.make(
                                        binding.root, "Please, Check your connection",
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .show()
                                }
                                else -> {

                                }
                            }
                        }
                    }
                } else if (SharedPreferenceSource.getInstance(this).getSavedMap() == "alert") {
                    SharedPreferenceSource.getInstance(this).setLatAndLonAlert(lat, lon)
                }else{
                    SharedPreferenceSource.getInstance(this).setLatAndLonHome(lat, lon)
                }
            }else{
                Snackbar.make(
                    binding.root, "Please, Check your connection",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0)

        mMap.addMarker(MarkerOptions().position(sydney))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.setOnMapClickListener {

            lat = it.latitude
            lon = it.longitude

            val snippet: String = java.lang.String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                it.latitude,
                it.longitude
            )
            mMap.addMarker(
                MarkerOptions()
                    .position(it)
                    .snippet(snippet)
            )
        }

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMapToolbarEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            checkLocationService()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

        mMap.setOnCameraMoveStartedListener (this)
        mMap.setOnCameraIdleListener (this)
        mMap.setOnCameraMoveListener  (this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled=true
            mMap.uiSettings.isMapToolbarEnabled=true
            mMap.uiSettings.isMyLocationButtonEnabled=true
            checkLocationService()
        }
    }

    private fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    private fun checkLocationService() {

        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10 * 1000
        locationRequest.fastestInterval = 2 * 1000


        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) {
            it.locationSettingsStates
            fetchCurrentLocation()
        }

        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        this@MapsActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }

            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra("result")
                fetchCurrentLocation()
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    override fun onCameraMoveStarted(p0: Int) {
        mMap.clear()
    }

    override fun onCameraMove() {
    }

    override fun onCameraIdle() {
        val markerOptions = MarkerOptions().position(mMap.cameraPosition.target)
        mMap.addMarker(markerOptions)
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