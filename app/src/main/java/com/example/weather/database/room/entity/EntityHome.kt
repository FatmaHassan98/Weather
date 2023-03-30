package com.example.weather.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weather.model.pojos.Current
import com.example.weather.model.pojos.Daily
import com.example.weather.model.pojos.Hourly

@Entity(tableName = "Home")
data class EntityHome(@PrimaryKey val id: Int = 1,
                      var lat:Double, var lon:Double, var current: Current?,
                      var hourly:List<Hourly>, var daily:List<Daily>) {

    constructor():this(1,0.0,0.0,null, emptyList(), emptyList())

}