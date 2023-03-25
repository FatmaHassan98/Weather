package com.example.weather.model.pojos

data class WeatherResponse(var lat:Double, var lon:Double, var timezone:String, var timezone_offset: Int
                           , var current: Current, var hourly:List<Hourly>, var daily:List<Daily>
                           , var alerts:List<Alert>) {
}