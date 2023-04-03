package com.example.weather.model

sealed class Status<out T>{

    class Success<T>(val data:T): Status<T>()
    class Failure(val message:Throwable): Status<Nothing>()
    object Loading: Status<Nothing>()

}
