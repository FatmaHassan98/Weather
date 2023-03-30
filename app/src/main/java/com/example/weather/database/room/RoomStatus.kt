package com.example.weather.database.room

import com.example.weather.database.room.entity.EntityHome

sealed class RoomStatus{

    class Success(val weather: EntityHome) : RoomStatus()
    class Failure(val message: Throwable) : RoomStatus()
    object Loading : RoomStatus()

}
