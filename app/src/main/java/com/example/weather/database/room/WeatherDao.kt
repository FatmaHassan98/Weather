package com.example.weather.database.room

import androidx.room.*
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.NotNull

@Dao
interface WeatherDao {

    @get:Query("SELECT * FROM Home")
    val getHomeWeather: Flow<EntityHome>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeWeather(entityHome: EntityHome)

    @get:Query("SELECT * FROM Favorite")
    val getFavorite : Flow<List<EntityFavorite>>
    @Insert
    suspend fun insertFavorite(entityFavorite: EntityFavorite)
    @Delete
    suspend fun deleteFavorite(entityFavorite: EntityFavorite)

    @get:Query("SELECT * FROM Alert")
    val getAlert : Flow<List<EntityAlert>>
    @Insert
    suspend fun insertAlert(entityAlert: EntityAlert)
    @Delete
    suspend fun deleteAlert(entityAlert: EntityAlert)
    @Query("SELECT * FROM Alert Where id=:id")
    fun getAlertById(id:String) : EntityAlert


}