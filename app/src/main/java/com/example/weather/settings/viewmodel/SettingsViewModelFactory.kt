package com.example.weather.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.database.shared.prefernces.SharedPreferenceSource

class SettingsViewModelFactory (private val sharedPreferenceSource: SharedPreferenceSource
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            SettingsViewModel(sharedPreferenceSource) as T
        }else{
            throw java.lang.IllegalArgumentException("ViewModel class not found")
        }
    }
}