package com.example.weather.model.repository

import com.example.weather.database.room.LocalSource
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome
import com.example.weather.model.pojos.WeatherResponse
import com.example.weather.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository private constructor(var remoteSource: RemoteSource,
                                     var localSource: LocalSource)
    : RepositoryInterface{

    companion object{

        private var instance : Repository? = null

        fun getInstance( remoteSource: RemoteSource,localSource: LocalSource): Repository {
            return instance?: synchronized(this){
                val _instance = Repository(remoteSource,localSource)
                instance = _instance

                _instance
            }
        }
    }

    override suspend fun getWeather(lat: Double,lon: Double,units: String,lang:String): Flow<WeatherResponse> {
        return flowOf( remoteSource.getWeather(lat,lon,units,lang) )
    }

    override suspend fun getWeatherAlert(lat: Double,lon: Double,units: String,lang:String): WeatherResponse {
        return remoteSource.getWeather(lat,lon,units,lang)
    }

    override val getHomeWeather: Flow<EntityHome> = localSource.getHomeWeather

    override suspend fun insertHomeWeather(entityHome: EntityHome) {
        localSource.insertHomeWeather(entityHome)
    }

    override val getFavorite: Flow<List<EntityFavorite>> = localSource.getFavorite
    override suspend fun insertFavorite(entityFavorite: EntityFavorite) {
        localSource.insertFavorite(entityFavorite)
    }
    override suspend fun deleteFavorite(entityFavorite: EntityFavorite) {
        localSource.deleteFavorite(entityFavorite)
    }

    override val getAlert: Flow<List<EntityAlert>> = localSource.getAlert
    override suspend fun insertAlert(entityAlert: EntityAlert) {
        localSource.insertAlert(entityAlert)
    }
    override suspend fun deleteAlert(entityAlert: EntityAlert) {
        localSource.deleteAlert(entityAlert)
    }
    override fun getAlertById(id: String): EntityAlert {
        return localSource.getAlertById(id)
    }

}