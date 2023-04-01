package com.example.weather.favorite.view

import com.example.weather.database.room.entity.EntityFavorite

interface FavoriteClickLisener {

    fun deleteFavorite(entityFavorite: EntityFavorite)
}