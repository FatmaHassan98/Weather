package com.example.weather.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.database.room.RoomStatus
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.home.model.GPSLocation
import com.example.weather.home.model.LocationStatus
import com.example.weather.model.repository.RepositoryInterface
import com.example.weather.network.APIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapViewModel (private val repositoryInterface: RepositoryInterface)
    : ViewModel() {

    val weather = MutableStateFlow<APIState>(APIState.Loading)
    val room = MutableStateFlow<RoomStatus>(RoomStatus.Loading)

    fun insertFavorite(entityFavorite: EntityFavorite){
        viewModelScope.launch {
            repositoryInterface.insertFavorite(entityFavorite)
        }
    }

    fun getWeather(lat: Double, lon: Double, units: String, lang:String) {
        viewModelScope.launch{
            repositoryInterface.getWeather(lat,lon,units,lang).catch {
                    e-> weather.value = APIState.Failure(e)
            }.collect {
                    data -> weather.value = APIState.Success(data)
            }
        }
    }

}