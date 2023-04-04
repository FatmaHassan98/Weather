package com.example.weather.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.favorite.viewmodel.FavoriteViewModel
import com.example.weather.home.model.GPSLocation
import com.example.weather.model.repository.RepositoryInterface

class MapViewModelFactory (private val repositoryInterface: RepositoryInterface,
                           private val gpsLocation: GPSLocation,
                           private val sharedPreferenceSource: SharedPreferenceSource
)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapViewModel::class.java)){
            MapViewModel(repositoryInterface) as T
        }else{
            throw java.lang.IllegalArgumentException("ViewModel class not found")
        }
    }
}