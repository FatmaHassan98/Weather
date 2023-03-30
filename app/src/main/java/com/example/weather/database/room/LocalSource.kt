package com.example.weather.database.room

import com.example.weather.database.room.entity.EntityHome
import kotlinx.coroutines.flow.Flow

interface LocalSource {

    suspend fun insertHomeWeather(entityHome: EntityHome)
    val getHomeWeather : Flow<EntityHome>

}