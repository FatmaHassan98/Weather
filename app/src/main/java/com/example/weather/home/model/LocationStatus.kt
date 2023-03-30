package com.example.weather.home.model

import android.location.Location

sealed class LocationStatus{
    class Success(val location: Location) : LocationStatus()
    class Failure(val message: Throwable) : LocationStatus()
    object Loading : LocationStatus()
}
