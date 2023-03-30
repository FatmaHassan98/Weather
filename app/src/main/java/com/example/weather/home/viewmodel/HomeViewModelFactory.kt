package com.example.weather.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.home.model.GPSLocation
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.model.repository.RepositoryInterface

class HomeViewModelFactory (private val repositoryInterface: RepositoryInterface,
                            private val gpsLocation: GPSLocation,
                            private val sharedPreferenceSource: SharedPreferenceSource)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(repositoryInterface,gpsLocation,sharedPreferenceSource) as T
        }else{
            throw java.lang.IllegalArgumentException("ViewModel class not found")
        }
    }
}