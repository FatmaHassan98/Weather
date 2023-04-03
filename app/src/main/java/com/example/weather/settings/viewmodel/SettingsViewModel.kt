package com.example.weather.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.database.shared.prefernces.Utaliltes

class SettingsViewModel(private val sharedPreferenceSource: SharedPreferenceSource)
    : ViewModel(){

    fun setSetting(location: String, language: String, temperature: String,
                   windSpeed: String, notification: String, theme: String) {

        sharedPreferenceSource.setSetting(location, language, temperature,
            windSpeed, notification, theme)
    }

    fun setLocationWay(location: String){
        sharedPreferenceSource.setLocationWay(location)
    }

    fun setLanguage(language: String){
        sharedPreferenceSource.setLanguage(language)
    }

    fun setUnit(unit:String){
        sharedPreferenceSource.setUnit(unit)
    }

    fun setNotification(notification: String){
        sharedPreferenceSource.setNotification(notification)
    }

    fun setTheme(theme: String){
        sharedPreferenceSource.setTheme(theme)
    }

    fun getSavedLocationWay(): String {
        return sharedPreferenceSource.getSavedLocationWay()
    }

    fun getSavedLanguage(): String {
        return sharedPreferenceSource.getSavedLanguage()
    }

    fun getSavedTemperatureUnit(): String {
        return sharedPreferenceSource.getSavedTemperatureUnit()
    }

    fun getSavedWindSpeedUnit(): String {
        return sharedPreferenceSource.getSavedWindSpeedUnit()
    }

    fun getSavedNotificationStatus(): String {
        return sharedPreferenceSource.getSavedNotificationStatus()
    }

    fun getSavedTheme(): String {
        return sharedPreferenceSource.getSavedTheme()
    }

}