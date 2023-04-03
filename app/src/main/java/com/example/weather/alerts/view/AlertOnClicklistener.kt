package com.example.weather.alerts.view

import com.example.weather.database.room.entity.EntityAlert

interface AlertOnClicklistener {

    fun deleteAlert(entityAlert: EntityAlert)

}