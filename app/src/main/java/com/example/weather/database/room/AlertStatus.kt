package com.example.weather.database.room

import com.example.weather.database.room.entity.EntityAlert

sealed class AlertStatus {

    class Success(val entityAlert: List<EntityAlert>) : AlertStatus()
    class Failure(val message: Throwable) : AlertStatus()
    object Loading : AlertStatus()

}