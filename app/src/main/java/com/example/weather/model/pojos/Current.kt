package com.example.weather.model.pojos

data class Current(var sunrise:Long, var temp:Double, var pressure:Int, var humidity:Int,
                   var clouds:Int, var visibility:Int, var wind_speed :Double,
                   var weather : List<Weather>, var dt: Long) {
}