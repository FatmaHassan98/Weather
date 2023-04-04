package com.example.weather.model.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weather.database.room.FackConceretLocalSource
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome
import com.example.weather.model.pojos.*
import com.example.weather.network.FackAPIClient
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.internal.matchers.Not

@RunWith(JUnit4::class)
class RepositoryTest{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val weather = listOf(Weather("","",""))
    private val hourly = listOf(Hourly(0,0.0,weather))
    private val daily = listOf(Daily(0, Temp(0.0,0.0),weather))
    private val alert = listOf(Alert("",0,"", "",0, listOf("")))
    private val current = Current(1,0.0,0,0,0,0,0.0,weather,0)

    private val home = EntityHome(1,30.0,30.0, current, hourly, daily)

    private val favorite1 = EntityFavorite(0,0.0,0.0,current, hourly, daily)
    private val favorite2 = EntityFavorite(1,0.0,0.0,current, hourly, daily)
    private val favorite3 = EntityFavorite(3,0.0,0.0,current, hourly, daily)

    private val alert1 = EntityAlert("0",0.0,0.0,current, alert ,"","","")
    private val alert2 = EntityAlert("1",0.0,0.0,current,alert,"","","")
    private val alert3 = EntityAlert("3",0.0,0.0,current,alert,"","","")


    private val remote = WeatherResponse(0.0,0.0,current,hourly,daily,alert)
    private val localHome = listOf(home).toMutableList()
    private val localFavorite = listOf(favorite1,favorite2,favorite3).toMutableList()
    private val localAlert = listOf(alert1,alert2,alert3).toMutableList()


    private lateinit var remoteSource : FackAPIClient
    private lateinit var localSource : FackConceretLocalSource
    private lateinit var repository: Repository


    @Before
    fun getSetup(){
        remoteSource = FackAPIClient(remote)
        localSource = FackConceretLocalSource(localHome,localFavorite,localAlert)
        repository = Repository.getInstance(remoteSource,localSource)
    }

    @Test
    fun getWeather_WeatherResponse() = runBlockingTest{
        var response  = 0.0
        repository.getWeather(0.0,0.0,"","").collect{
            response = it.lat
        }
        assertThat(response,IsEqual(remote.lat))
    }

    @Test
    fun getWeatherAlert_WeatherResponse()= runBlockingTest {
        var response = repository.getWeatherAlert(0.0,0.0,"","").lat
        assertThat(response,IsEqual(remote.lat))
    }

    @Test
    fun getFavorite_EntityFavorite()= runBlockingTest{
        var favorite = 0.0
         repository.getFavorite.collect{
             favorite = it[0].lat
         }
        assertThat(favorite,IsEqual(localFavorite[0].lat))
    }

    @Test
    fun getAlert_EntityAlert()= runBlockingTest {
        var alert = 0.0
        repository.getAlert.collect{
            alert = it[0].lat
        }
        assertThat(alert,IsEqual(localAlert[0].lat))
    }

    @Test
    fun getHomeWeather_EntityHome()= runBlockingTest{
        var home = 0.0
        repository.getHomeWeather.collect{
            home = it.lat
        }
        assertThat(home,IsEqual(localHome[0].lat))
    }

    @Test
    fun getAlertById_Id_EntityAlert()= runBlockingTest {
        var alert = repository.getAlertById(localAlert[0].id).lat
        assertThat(alert,IsEqual(localAlert[0].lat))
    }

    @Test
    fun insertHomeWeather() = runBlockingTest{
        val home = EntityHome(2,30.0,30.0, current, hourly, daily)
        repository.insertHomeWeather(home)
        var home2 = 0
        repository.getHomeWeather.collect{
            home2 = it.id
        }
        assertThat(home2,IsEqual(2))
    }

    @Test
    fun insertFavorite()= runBlockingTest {
        val favorite = EntityFavorite(5,0.0,0.0,current, hourly, daily)
        repository.insertFavorite(favorite)

        var favorite1 = 0
        repository.getFavorite.collect{
            favorite1 = it[it.size-1].id
        }
        assertThat(favorite1,IsEqual(5))
    }

    @Test
    fun deleteFavorite()= runBlockingTest {
        val favorite = EntityFavorite(5,0.0,0.0,current, hourly, daily)
        repository.insertFavorite(favorite)
        repository.deleteFavorite(favorite)
        var favorite1 = 0
        repository.getFavorite.collect{
            favorite1 = it[it.size-1].id
        }
        assertThat(favorite1,IsEqual(3))
    }

    @Test
    fun insertAlert()= runBlockingTest {
        val alert = EntityAlert("5",0.0,0.0,current, alert ,"","","")
        repository.insertAlert(alert)

        var alert1 = ""
        repository.getAlert.collect{
            alert1 = it[it.size-1].id
        }
        assertThat(alert1,IsEqual("5"))
    }

    @Test
    fun deleteAlert()= runBlockingTest {
        val alert = EntityAlert("5",0.0,0.0,current, alert ,"","","")
        repository.insertAlert(alert)
        repository.deleteAlert(alert)

        var alert1 = ""
        repository.getAlert.collect{
            alert1 = it[it.size-1].id
        }
        assertThat(alert1,IsEqual("3"))
    }

}