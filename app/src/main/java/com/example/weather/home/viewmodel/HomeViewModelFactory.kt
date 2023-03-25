package com.example.weather.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.repository.RepositoryInterface

class HomeViewModelFactory (private val repositoryInterface: RepositoryInterface) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(repositoryInterface) as T
        }else{
            throw java.lang.IllegalArgumentException("ViewModel class not found")
        }
    }
}