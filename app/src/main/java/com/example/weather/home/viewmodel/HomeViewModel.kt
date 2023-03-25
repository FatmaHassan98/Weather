package com.example.weather.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.repository.RepositoryInterface
import com.example.weather.network.APIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel (private val repositoryInterface: RepositoryInterface) : ViewModel() {

    private val state = APIState.Loading

    val weather = MutableStateFlow<APIState>(state)

    init {
        getWeather(31.3439167,30.3915117,"metric","en")
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
}