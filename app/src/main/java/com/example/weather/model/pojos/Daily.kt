package com.example.weather.model.pojos

data class Daily (var dt: Long, var temp: Temp, var weather: List<Weather>) {
}