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
    }
    fun setLocationWay(location: String){
        editor!!.putString(Utaliltes.LOCATION, location)
        editor!!.commit()
    }

    fun setLanguage(language: String){
        editor!!.putString(Utaliltes.LANGUAGE, language)
        editor!!.commit()
    }

    fun setUnit(unit:String){
        editor!!.putString(Utaliltes.UNIT, unit)
        editor!!.commit()
    }

    fun setNotification(notification: String){
        editor!!.putString(Utaliltes.NOTIFICATION, notification)
        editor!!.commit()
    }

    fun setTheme(theme: String){
        editor!!.putString(Utaliltes.THEME, theme)
        editor!!.commit()
    }
    fun setMap(map:String){
        editor!!.putString(Utaliltes.MAP,map)
        editor!!.commit()
    }
    fun setLatAndLon(lat:Double,lon:Double){
        editor!!.putFloat(Utaliltes.MAP_VALUE_LAT,lat.toFloat())
        editor!!.putFloat(Utaliltes.MAP_VALUE_LON,lon.toFloat())
        editor!!.commit()
    }
    fun setLatAndLonHome(lat:Double,lon:Double){
        editor!!.putFloat(Utaliltes.LAT_HOME,lat.toFloat())
        editor!!.putFloat(Utaliltes.LON_HOME,lon.toFloat())
        editor!!.commit()
    }

    fun setLatAndLonAlert(lat:Double,lon:Double){
        editor!!.putFloat(Utaliltes.LAT_ALERT,lat.toFloat())
        editor!!.putFloat(Utaliltes.LON_ALERT,lon.toFloat())
        editor!!.commit()
    }

    fun getLatAlert():Double{
        return return sharedPreferences!!.getFloat(Utaliltes.LAT_ALERT,0.0f).toDouble()
    }

    fun getLonAlert():Double{
        return return sharedPreferences!!.getFloat(Utaliltes.LON_ALERT,0.0f).toDouble()
    }

    fun getLatHome():Double{
        return return sharedPreferences!!.getFloat(Utaliltes.LAT_HOME,0.0f).toDouble()
    }

    fun getLonHome():Double{
        return return sharedPreferences!!.getFloat(Utaliltes.LON_HOME,0.0f).toDouble()
    }
    fun getLat():Double{
        return sharedPreferences!!.getFloat(Utaliltes.MAP_VALUE_LAT,0.0f).toDouble()
    }
    fun getLon():Double{
        return sharedPreferences!!.getFloat(Utaliltes.MAP_VALUE_LON,0.0f).toDouble()
    }
    fun getSavedLocationWay(): String {
        return sharedPreferences!!.getString(Utaliltes.LOCATION, "")!!
    }
    fun getSavedLanguage(): String {
        return sharedPreferences!!.getString(Utaliltes.LANGUAGE, "en")!!
    }
    fun getSavedTemperatureUnit(): String {
        return sharedPreferences!!.getString(Utaliltes.TEMPERATURE, "metric")!!
    }
    fun getSavedWindSpeedUnit(): String {
        return sharedPreferences!!.getString(Utaliltes.WIND_SPEED, "metric")!!
    }
    fun getSavedNotificationStatus(): String {
        return sharedPreferences!!.getString(Utaliltes.NOTIFICATION, "")!!
    }
    fun getSavedTheme(): String {
        return sharedPreferences!!.getString(Utaliltes.THEME, "light")!!
    }
    fun getSavedMap(): String? {
        return sharedPreferences!!.getString(Utaliltes.MAP,"")
    }

    fun getSavedUnit():String{
        return sharedPreferences?.getString(Utaliltes.UNIT,"metric").toString()
    }

}