package com.example.weather.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.database.room.RoomStatus
import com.example.weather.database.room.entity.EntityHome
import com.example.weather.home.model.GPSLocation
import com.example.weather.database.shared.prefernces.SharedPreferenceSource
import com.example.weather.home.model.LocationStatus
import com.example.weather.model.repository.RepositoryInterface
import com.example.weather.network.APIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel (private val repositoryInterface: RepositoryInterface,
                     private val gpsLocation: GPSLocation,
                     private val sharedPreferenceSource: SharedPreferenceSource)
    : ViewModel() {

    private val state = APIState.Loading

    val weather = MutableStateFlow<APIState>(state)
    val room = MutableStateFlow<RoomStatus>(RoomStatus.Loading)

    init {
        getLocation()
        getHomeWeather()
    }

     private fun getWeather(lat: Double, lon: Double, units: String, lang:String) {
        viewModelScope.launch{
            repositoryInterface.getWeather(lat,lon,units,lang).catch {
                    e-> weather.value = APIState.Failure(e)
            }.collect {
                    data -> weather.value = APIState.Success(data)
            }
        }
    }
    private fun getLocation(){
        viewModelScope.launch{
            gpsLocation.location.collectLatest {
                when(it){
                    is LocationStatus.Success -> {
                        getWeather(it.location.latitude , it.location.longitude,
                            sharedPreferenceSource.getSavedTemperatureUnit(),
                            sharedPreferenceSource.getSavedLanguage())
                    }
                    is LocationStatus.Failure ->{

                    }else -> {

                    }
                }
            }

        }
    }

    fun insertHomeWeather(entityHome: EntityHome){
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.insertHomeWeather(entityHome)
        }
    }
    private fun getHomeWeather(){
        viewModelScope.launch(Dispatchers.IO)  {
            repositoryInterface.getHomeWeather.catch {
                e-> room.value = RoomStatus.Failure(e)
            }.collect{
                data -> room.value = RoomStatus.Success(data)
            }
        }
    }
}