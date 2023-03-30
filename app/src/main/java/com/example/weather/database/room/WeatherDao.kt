package com.example.weather.database.room

import androidx.room.*
import com.example.weather.database.room.entity.EntityHome
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @get:Query("SELECT * FROM Home")
    val getHomeWeather: Flow<EntityHome>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeWeather(entityHome: EntityHome)

}