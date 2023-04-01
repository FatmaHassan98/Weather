package com.example.weather.database.room

import androidx.room.Delete
import androidx.room.Insert
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome
import kotlinx.coroutines.flow.Flow

interface LocalSource {

    suspend fun insertHomeWeather(entityHome: EntityHome)
    val getHomeWeather : Flow<EntityHome>

    val getFavorite : Flow<List<EntityFavorite>>
    suspend fun insertFavorite(entityFavorite: EntityFavorite)
    suspend fun deleteFavorite(entityFavorite: EntityFavorite)

}