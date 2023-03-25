package com.example.weather.network

import com.example.weather.model.pojos.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("onecall?appid=87efa574d0e1e382f33b0f42eaba9da1&exclude=minutely")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang:String
    ) : WeatherResponse
}