package com.example.weather.model.repository

import com.example.weather.model.pojos.WeatherResponse
import com.example.weather.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class Repository private constructor(var remoteSource: RemoteSource) : RepositoryInterface{

    companion object{

        private var instance : Repository? = null

        fun getInstance( remoteSource: RemoteSource): Repository {
            return instance?: synchronized(this){
                val _instance = Repository(remoteSource)
                instance = _instance

                _instance
            }
        }
    }

    override suspend fun getWeather(lat: Double,lon: Double,units: String,lang:String): Flow<WeatherResponse> {

        return flowOf( remoteSource.getWeather(lat,lon,units,lang) )
    }
}