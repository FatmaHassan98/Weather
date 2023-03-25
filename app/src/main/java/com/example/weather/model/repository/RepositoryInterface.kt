package com.example.weather.model.repository

import com.example.weather.model.pojos.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {

    suspend fun getWeather(lat: Double,lon: Double,units: String,lang:String) : Flow<WeatherResponse>
}