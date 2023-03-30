package com.example.weather.database.room.converter

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.weather.model.pojos.*
import com.google.gson.Gson

class Converter {

    @TypeConverter
    fun fromCurrentToString(current: Current): String = Gson().toJson(current)

    @TypeConverter
    fun fromStringToCurrent(current: String): Current = Gson().fromJson(current,Current::class.java)

    @TypeConverter
    fun fromTempToString(temp: Temp):String = Gson().toJson(temp)

    @TypeConverter
    fun fromStringToTemp(temp:String):Temp = Gson().fromJson(temp,Temp::class.java)

    @TypeConverter
    fun fromWeatherToString(weather: Weather):String = Gson().toJson(weather)

    @TypeConverter
    fun fromStringToWeather(weather:String):Weather = Gson().fromJson(weather,Weather::class.java)

    @TypeConverter
    fun fromWeatherListToString(weather: List<Weather>) :String= Gson().toJson(weather)

    @TypeConverter
    fun fromStringToWeatherList(weather: String) = Gson().fromJson(weather, Array<Weather>::class.java).toList()

    @TypeConverter
    fun fromHourlyToString(hourly: List<Hourly>) :String= Gson().toJson(hourly)

    @TypeConverter
    fun fromStringToHourly(hourly: String) = Gson().fromJson(hourly, Array<Hourly>::class.java).toList()

    @TypeConverter
    fun fromDailyToString(daily: List<Daily>) :String= Gson().toJson(daily)

    @TypeConverter
    fun fromStringToDaily(daily: String) = Gson().fromJson(daily, Array<Daily>::class.java).toList()
}