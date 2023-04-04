package com.example.weather.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weather.MainRule
import com.example.weather.database.room.FavoriteStatus
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.map.viewmodel.MapViewModel
import com.example.weather.model.pojos.*
import com.example.weather.model.repository.FackRepository
import com.example.weather.network.APIState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FavoriteViewModelTest{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val main = MainRule()

    private lateinit var favoriteViewModel : FavoriteViewModel
    lateinit var repository : FackRepository
    lateinit var mapViewModel: MapViewModel

    @Before
    fun setUp(){
        repository = FackRepository()
        favoriteViewModel = FavoriteViewModel(repository)
        mapViewModel = MapViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getFavorite_EntityFavorite()= runBlockingTest {

        val weather = listOf(Weather("","",""))
        val hourly = listOf(Hourly(0,0.0,weather))
        val daily = listOf(Daily(0, Temp(0.0,0.0),weather))
        val current = Current(1,0.0,0,0,0,0,0.0,weather,0)

        val favorite1 = EntityFavorite(2,0.0,0.0,current, hourly, daily)

        mapViewModel.insertFavorite(favorite1)

        favoriteViewModel.getFavorite()


        launch {

            favoriteViewModel.favorite.collect {
                when (it) {
                    is FavoriteStatus.Success -> {
                        if (it.entityFavorite.isNotEmpty()) {
                            assertThat(it.entityFavorite, `is`(listOf(favorite1)))
                            cancel()
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteFavorite() = runBlockingTest {
        val weather = listOf(Weather("","",""))
        val hourly = listOf(Hourly(0,0.0,weather))
        val daily = listOf(Daily(0, Temp(0.0,0.0),weather))
        val current = Current(1,0.0,0,0,0,0,0.0,weather,0)

        val favorite1 = EntityFavorite(2,0.0,0.0,current, hourly, daily)
        val favorite2 = EntityFavorite(3,0.0,0.0,current, hourly, daily)

        mapViewModel.insertFavorite(favorite1)
        mapViewModel.insertFavorite(favorite2)

        favoriteViewModel.deleteFavorite(favorite1)
        favoriteViewModel.getFavorite()


        launch {

            favoriteViewModel.favorite.collect {
                when (it) {
                    is FavoriteStatus.Success -> {
                        if (it.entityFavorite.isNotEmpty()) {
                            assertThat(it.entityFavorite[0].id, `is`(3))
                            cancel()
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    @Test
    fun getWeather() = runBlockingTest{
        favoriteViewModel.getWeather(0.0,0.0,"","")

        launch {
            favoriteViewModel.weather.collect {
                when (it) {
                    is APIState.Success -> {
                        assertThat(it.weather.lat, IsEqual(0.0))
                        cancel()
                    }
                    else -> {

                    }
                }
            }
        }
    }
}