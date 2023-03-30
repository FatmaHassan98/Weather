package com.example.weather.model.pojos

data class Hourly ( val dt: Long, val temp: Double, val weather: List<Weather>) {
}