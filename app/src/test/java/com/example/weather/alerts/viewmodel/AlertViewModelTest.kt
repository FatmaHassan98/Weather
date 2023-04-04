package com.example.weather.alerts.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weather.MainRule
import com.example.weather.database.room.AlertStatus
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.model.pojos.*
import com.example.weather.model.repository.FackRepository
import com.example.weather.network.APIState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class AlertViewModelTest{
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val main = MainRule()

    private lateinit var alertViewModel: AlertViewModel
    private lateinit var repository : FackRepository

    @Before
    fun getSetup(){
        repository = FackRepository()
        alertViewModel = AlertViewModel(repository)
    }

    @Test
    fun getAlert()= runBlockingTest {

        val weather = listOf(Weather("","",""))
        val alert1 = listOf(Alert("",0,"", "",0, listOf("")))
        val current = Current(1,0.0,0,0,0,0,0.0,weather,0)

        val alert = EntityAlert("1",0.0,0.0,current,alert1,"","","")

        alertViewModel.insertAlert(alert)

        alertViewModel.getAlert()

        launch {
            alertViewModel.alert.collect {
                when (it) {
                    is AlertStatus.Success -> {
                        assertThat(it.entityAlert, CoreMatchers.`is`(listOf(alert)))
                        cancel()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    @Test
    fun insertAlert() = runBlockingTest {
        val weather = listOf(Weather("","",""))
        val alert1 = listOf(Alert("",0,"", "",0, listOf("")))
        val current = Current(1,0.0,0,0,0,0,0.0,weather,0)

        val alert = EntityAlert("1",0.0,0.0,current,alert1,"","","")

        alertViewModel.insertAlert(alert)

        alertViewModel.getAlert()

        launch {
            alertViewModel.alert.collect {
                when (it) {
                    is AlertStatus.Success -> {
                        assertThat(it.entityAlert, CoreMatchers.`is`(listOf(alert)))
                        cancel()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    @Test
    fun deleteAlert() = runBlockingTest{

        val weather = listOf(Weather("","",""))
        val alert1 = listOf(Alert("",0,"", "",0, listOf("")))
        val current = Current(1,0.0,0,0,0,0,0.0,weather,0)

        val alert = EntityAlert("1",0.0,0.0,current,alert1,"","","")

        val alert2 = EntityAlert("2",0.0,0.0,current,alert1,"","","")

        alertViewModel.insertAlert(alert)

        alertViewModel.insertAlert(alert2)

        alertViewModel.deleteAlert(alert)

        alertViewModel.getAlert()

        launch {
            alertViewModel.alert.collect {
                when (it) {
                    is AlertStatus.Success -> {
                        assertThat(it.entityAlert, CoreMatchers.`is`(listOf(alert2)))
                        cancel()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    @Test
    fun getWeather() = runBlockingTest{

        alertViewModel.getWeather(0.0,0.0,"","")

        launch {
            alertViewModel.weather.collect {
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