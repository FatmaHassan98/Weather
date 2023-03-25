package com.example.weather.network

import com.example.weather.model.pojos.WeatherResponse

sealed class APIState{

    class Success(val weather: WeatherResponse) : APIState()
    class Failure(val message: Throwable) : APIState()
    object Loading : APIState()

}
