package com.example.weather.model.repository

import androidx.room.Delete
import androidx.room.Insert
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome
import com.example.weather.model.pojos.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {

    suspend fun getWeather(lat: Double,lon: Double,units: String,lang:String) : Flow<WeatherResponse>

    val getHomeWeather : Flow<EntityHome>
    suspend fun insertHomeWeather(entityHome: EntityHome)

    val getFavorite : Flow<List<EntityFavorite>>
    suspend fun insertFavorite(entityFavorite: EntityFavorite)
    suspend fun deleteFavorite(entityFavorite: EntityFavorite)

    val getAlert : Flow<List<EntityAlert>>
    suspend fun insertAlert(entityAlert: EntityAlert)
    suspend fun deleteAlert(entityAlert: EntityAlert)
    fun getAlertById(id:String):Flow<EntityAlert>
}