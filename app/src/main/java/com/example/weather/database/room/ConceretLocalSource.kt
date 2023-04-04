package com.example.weather.database.room

import android.content.Context
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome
import kotlinx.coroutines.flow.Flow

class ConceretLocalSource(private val weatherDao :WeatherDao) : LocalSource {

    override suspend fun insertHomeWeather(entityHome: EntityHome) {
        weatherDao.insertHomeWeather(entityHome)
    }

    override val getHomeWeather: Flow<EntityHome> = weatherDao.getHomeWeather

    override val getFavorite: Flow<List<EntityFavorite>> = weatherDao.getFavorite

    override suspend fun insertFavorite(entityFavorite: EntityFavorite) {
        weatherDao.insertFavorite(entityFavorite)
    }

    override suspend fun deleteFavorite(entityFavorite: EntityFavorite) {
        weatherDao.deleteFavorite(entityFavorite)
    }

    override val getAlert: Flow<List<EntityAlert>> = weatherDao.getAlert

    override suspend fun insertAlert(entityAlert: EntityAlert) {
        weatherDao.insertAlert(entityAlert)
    }

    override suspend fun deleteAlert(entityAlert: EntityAlert) {
        weatherDao.deleteAlert(entityAlert)
    }
    override fun getAlertById(id: String): EntityAlert {
        return weatherDao.getAlertById(id)
    }


}