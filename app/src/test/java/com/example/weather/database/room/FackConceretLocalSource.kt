package com.example.weather.database.room

import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class FackConceretLocalSource(var home : MutableList<EntityHome> = mutableListOf(),
                              val favorite : MutableList<EntityFavorite> = mutableListOf(),
                              val alert : MutableList<EntityAlert> = mutableListOf() )
    : LocalSource {

    override suspend fun insertHomeWeather(entityHome: EntityHome) {
        home.removeFirst()
        home.add(entityHome)
    }

    override val getHomeWeather: Flow<EntityHome> = home.let {
         return@let it.asFlow()
    }

    override val getFavorite: Flow<List<EntityFavorite>> = favorite.let {
        return@let flowOf(it)
    }

    override suspend fun insertFavorite(entityFavorite: EntityFavorite) {
        favorite.add(entityFavorite)
    }

    override suspend fun deleteFavorite(entityFavorite: EntityFavorite) {
        favorite.remove(entityFavorite)
    }

    override val getAlert: Flow<List<EntityAlert>> = alert.let {
        return@let flowOf(it)
    }

    override suspend fun insertAlert(entityAlert: EntityAlert) {
        alert.add(entityAlert)
    }

    override suspend fun deleteAlert(entityAlert: EntityAlert) {
        alert.remove(entityAlert)
    }

    override fun getAlertById(id: String): EntityAlert {
        return alert[id.toInt()]
    }
}