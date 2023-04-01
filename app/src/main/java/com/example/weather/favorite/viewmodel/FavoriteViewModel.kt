package com.example.weather.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.database.room.FavoriteStatus
import com.example.weather.database.room.RoomStatus
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome
import com.example.weather.model.repository.RepositoryInterface
import com.example.weather.network.APIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteViewModel (private val repositoryInterface: RepositoryInterface)
    : ViewModel() {

    val favorite = MutableStateFlow<FavoriteStatus>(FavoriteStatus.Loading)
    val weather = MutableStateFlow<APIState>(APIState.Loading)

    init {
        getFavorite()
    }
    fun deleteFavorite(entityFavorite: EntityFavorite){
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.deleteFavorite(entityFavorite)
        }
    }
    private fun getFavorite(){
        viewModelScope.launch(Dispatchers.IO)  {
            repositoryInterface.getFavorite.catch {
                    e-> favorite.value = FavoriteStatus.Failure(e)
            }.collect{
                    data -> favorite.value = FavoriteStatus.Success(data)
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