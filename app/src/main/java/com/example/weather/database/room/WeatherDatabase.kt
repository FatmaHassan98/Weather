package com.example.weather.database.room

import android.content.Context
import androidx.room.*
import com.example.weather.database.room.converter.Converter
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome

@Database(entities = [EntityHome::class,EntityFavorite::class], version = 2)
@TypeConverters(Converter::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun getHomeWeather() : WeatherDao

    companion object{
        @Volatile
        private var INSTANCE : WeatherDatabase? = null

        fun getInstance(context: Context):WeatherDatabase{
            return INSTANCE ?: synchronized(context){
                val instance = Room.databaseBuilder(
                    context.applicationContext , WeatherDatabase::class.java,"Weather")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }

        }
    }
}