package com.example.weather.network
import com.example.weather.model.pojos.WeatherResponse

interface RemoteSource {
    suspend fun getWeather(lat: Double, lon: Double, units: String, lang:String) : WeatherResponse
}