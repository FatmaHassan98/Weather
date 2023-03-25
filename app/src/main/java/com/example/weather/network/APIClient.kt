package com.example.weather.network

import com.example.weather.model.pojos.WeatherResponse

class APIClient private constructor(): RemoteSource {

    private val weatherService : WeatherService by lazy {
        RetrofitHelper.API.retrofitService
    }

    companion object{
        private var instance : APIClient? = null

        fun getInstance(): APIClient {
            return instance?: synchronized(this){
                val _instance = APIClient()
                instance = _instance
                _instance
            }
        }
    }

    override suspend fun getWeather(lat: Double, lon: Double, units: String,
                                    lang:String): WeatherResponse {

        return weatherService.getWeather(lat,lon,units,lang)
    }


}