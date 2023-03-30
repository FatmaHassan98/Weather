package com.example.weather.home.model

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class GPSLocation private constructor(context: Context){

    private var fusedLocationClient: FusedLocationProviderClient
    val location = MutableStateFlow<LocationStatus>(LocationStatus.Loading)

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    companion object{
        @Volatile
        private var INSTANCE : GPSLocation? = null
        fun getInstance(context: Context) : GPSLocation {
            return INSTANCE ?: synchronized(context){
                val instance = GPSLocation(context)
                INSTANCE = instance
                instance
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        requestNewLocationData()
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0L).apply {
            setMinUpdateIntervalMillis(100)
        }.build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val  locationCallback : LocationCallback = object : LocationCallback() {
        override fun onLocationResult( mLocationRequest: LocationResult) {
            val mLastLocation : Location? = mLocationRequest.lastLocation
            if (mLastLocation!=null){
                location.value = LocationStatus.Success(mLastLocation)
                removeLocationUpdate()
            }else{
                location.value = LocationStatus.Failure(Throwable())
            }
        }
    }

    private fun removeLocationUpdate(){
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}