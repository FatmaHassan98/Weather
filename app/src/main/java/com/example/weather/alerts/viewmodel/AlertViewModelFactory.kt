package com.example.weather.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.repository.RepositoryInterface

class AlertViewModelFactory (private val repositoryInterface: RepositoryInterface)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)){
            AlertViewModel(repositoryInterface) as T
        }else{
            throw java.lang.IllegalArgumentException("ViewModel class not found")
        }
    }
}