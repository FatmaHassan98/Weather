package com.example.weather.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.repository.RepositoryInterface

class FavoriteViewModelFactory (private val repositoryInterface: RepositoryInterface)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            FavoriteViewModel(repositoryInterface) as T
        }else{
            throw java.lang.IllegalArgumentException("ViewModel class not found")
        }
    }
}