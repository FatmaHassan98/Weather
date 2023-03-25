package com.example.weather.network
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private const val baseUrl = "https://api.openweathermap.org/data/2.5/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    object API{
        val retrofitService : WeatherService by lazy {
            RetrofitHelper.getInstance().create(WeatherService::class.java)
        }
    }
}