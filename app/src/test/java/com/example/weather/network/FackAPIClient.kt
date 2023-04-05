package com.example.weather.network

import com.example.weather.model.pojos.WeatherResponse

class FackAPIClient(val response: WeatherResponse)  : RemoteSource{
    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String,
    ): WeatherResponse {
        return response
    }
}