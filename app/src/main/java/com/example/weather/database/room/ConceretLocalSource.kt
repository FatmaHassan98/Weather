package com.example.weather.database.room

import android.content.Context
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome
import kotlinx.coroutines.flow.Flow

class ConceretLocalSource(context: Context) : LocalSource {

    private val weatherDao :WeatherDao by lazy {
        val appDataBase: WeatherDatabase = WeatherDatabase.getInstance(context)
        appDataBase.getHomeWeather()
    }

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


}