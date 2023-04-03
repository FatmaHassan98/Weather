package com.example.weather.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.database.room.AlertStatus
import com.example.weather.database.room.FavoriteStatus
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.model.repository.RepositoryInterface
import com.example.weather.network.APIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel (private val repositoryInterface: RepositoryInterface)
    : ViewModel() {

    val alert = MutableStateFlow<AlertStatus>(AlertStatus.Loading)
    val weather = MutableStateFlow<APIState>(APIState.Loading)

    init {
        getAlert()
    }

    fun insertAlert(entityAlert: EntityAlert) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.insertAlert(entityAlert)
        }
    }

    fun deleteAlert(entityAlert: EntityAlert){
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.deleteAlert(entityAlert)
        }
    }

    private fun getAlert(){
        viewModelScope.launch(Dispatchers.IO)  {
            repositoryInterface.getAlert.catch {
                    e-> alert.value = AlertStatus.Failure(e)
            }.collect{
                    data -> alert.value = AlertStatus.Success(data)
            }
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