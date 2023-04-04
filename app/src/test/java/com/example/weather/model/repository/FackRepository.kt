package com.example.weather.model.repository

import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome
import com.example.weather.model.pojos.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FackRepository :RepositoryInterface{

    var home : MutableList<EntityHome> = mutableListOf()
    var favorite : MutableList<EntityFavorite> = mutableListOf()
    var alert : MutableList<EntityAlert> = mutableListOf()

    private val weather = listOf(Weather("","",""))
    private val hourly = listOf(Hourly(0,0.0,weather))
    private val daily = listOf(Daily(0, Temp(0.0,0.0),weather))
    private val alert1 = listOf(Alert("",0,"", "",0, listOf("")))
    private val current = Current(1,0.0,0,0,0,0,0.0,weather,0)


    override val getHomeWeather: Flow<EntityHome> = flowOf()

    override val getFavorite: Flow<List<EntityFavorite>> = flowOf(favorite)

    override val getAlert: Flow<List<EntityAlert>> = flowOf(alert)

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String,
    ): Flow<WeatherResponse> {
        return  flowOf(WeatherResponse(0.0,0.0, current,hourly,daily,alert1))
    }

    override suspend fun getWeatherAlert(
        lat: Double,
        lon: Double,
        units: String,
        lang: String,
    ): WeatherResponse {
        return  WeatherResponse(0.0,0.0, current,hourly,daily,alert1)
    }

    override suspend fun insertHomeWeather(entityHome: EntityHome) {
        home.add(entityHome)
    }

    override suspend fun insertFavorite(entityFavorite: EntityFavorite) {
        favorite.add(entityFavorite)
    }

    override suspend fun deleteFavorite(entityFavorite: EntityFavorite) {
        favorite.remove(entityFavorite)
    }

    override suspend fun insertAlert(entityAlert: EntityAlert) {
        alert.add(entityAlert)
    }

    override suspend fun deleteAlert(entityAlert: EntityAlert) {
        alert.remove(entityAlert)
    }

    override fun getAlertById(id: String): EntityAlert {
        return alert[id.toInt()]
    }
}