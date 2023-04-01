package com.example.weather.database.room

import com.example.weather.database.room.entity.EntityFavorite

sealed class FavoriteStatus{
    class Success(val entityFavorite: List<EntityFavorite>) : FavoriteStatus()
    class Failure(val message: Throwable) : FavoriteStatus()
    object Loading : FavoriteStatus()
}
