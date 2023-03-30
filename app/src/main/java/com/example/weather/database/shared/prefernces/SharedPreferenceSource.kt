package com.example.weather.database.shared.prefernces

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceSource private constructor(context: Context){

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    init {
        sharedPreferences = context.getSharedPreferences(Utaliltes.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
    }

   companion object {
       @Volatile
       private var INSTANCE : SharedPreferenceSource? = null

       fun getInstance(context: Context) : SharedPreferenceSource{
           return INSTANCE ?: synchronized(context){
               val instance = SharedPreferenceSource(context)
               INSTANCE = instance

               instance
           }

       }
   }

    fun setSetting(location: String, language: String, temperature: String,
                   windSpeed: String, notification: String, theme: String) {
        editor!!.putString(Utaliltes.LOCATION, location)
        editor!!.putString(Utaliltes.LANGUAGE, language)
        editor!!.putString(Utaliltes.TEMPERATURE, temperature)
        editor!!.putString(Utaliltes.WIND_SPEED, windSpeed)
        editor!!.putString(Utaliltes.NOTIFICATION, notification)
        editor!!.putString(Utaliltes.THEME, theme)
        editor!!.commit()

        println("Location "+sharedPreferences!!.getString(Utaliltes.LOCATION, "GPS")!!)
    }

    fun getSavedLocationWay(): String {
        return sharedPreferences!!.getString(Utaliltes.LOCATION, "GPS")!!
    }

    fun getSavedLanguage(): String {
        return sharedPreferences!!.getString(Utaliltes.LANGUAGE, "english")!!
    }

    fun getSavedTemperatureUnit(): String {
        return sharedPreferences!!.getString(Utaliltes.TEMPERATURE, "metric")!!
    }

    fun getSavedWindSpeedUnit(): String {
        return sharedPreferences!!.getString(Utaliltes.WIND_SPEED, "metric")!!
    }

    fun getSavedNotificationStatus(): String {
        return sharedPreferences!!.getString(Utaliltes.NOTIFICATION, "enable")!!
    }

    fun getSavedTheme(): String {
        return sharedPreferences!!.getString(Utaliltes.THEME, "light")!!
    }

}