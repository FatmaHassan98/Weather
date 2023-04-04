package com.example.weather.database.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weather.database.room.entity.EntityAlert
import com.example.weather.database.room.entity.EntityFavorite
import com.example.weather.database.room.entity.EntityHome
import com.example.weather.model.pojos.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {

    private lateinit var database : WeatherDatabase

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()
    }

    @After
    fun deleteDatabase()  = database.close()


    @Test
    fun insertWeatherHomeAndGetWeatherHome() = runBlockingTest {
        val weather = listOf(Weather("","",""))
        val hourly = listOf(Hourly(0,0.0,weather))
        val daily = listOf(Daily(0, Temp(0.0,0.0),weather))
        val current = Current(1,0.0,0,0,0,0,0.0,weather,0)

        val weather1 = EntityHome(2,0.0,0.0, current,hourly,daily)

        database.getHomeWeather().insertHomeWeather(weather1)

        launch {
            database.getHomeWeather().getHomeWeather.collect {
                Assert.assertThat(it.id, IsEqual(2))
                cancel()
            }
        }
    }

    @Test
    fun getWeatherHome() = runBlockingTest{
        val weather = listOf(Weather("","",""))
        val hourly = listOf(Hourly(0,0.0,weather))
        val daily = listOf(Daily(0, Temp(0.0,0.0),weather))
        val current = Current(1,0.0,0,0,0,0,0.0,weather,0)

        val weather1 = EntityHome(1,0.0,0.0, current,hourly,daily)

        database.getHomeWeather().insertHomeWeather(weather1)

        launch {
            database.getHomeWeather().getHomeWeather.collect {
                Assert.assertThat(it.id, IsEqual(1))
                cancel()
            }
        }

    }

    @Test
    fun insertFavorite() = runBlockingTest {
        val weather = listOf(Weather("","",""))
        val hourly = listOf(Hourly(0,0.0,weather))
        val daily = listOf(Daily(0, Temp(0.0,0.0),weather))
        val current = Current(1,0.0,0,0,0,0,0.0,weather,0)

        val favorite = EntityFavorite(1,0.0,0.0, current,hourly,daily)

        database.getHomeWeather().insertFavorite(favorite)

        launch {
            database.getHomeWeather().getFavorite.collect{
                Assert.assertThat(it[0].id ,IsEqual(1))
                cancel()
            }
        }
    }

    @Test
    fun deleteFavorite() = runBlockingTest {
        val weather = listOf(Weather("", "", ""))
        val hourly = listOf(Hourly(0, 0.0, weather))
        val daily = listOf(Daily(0, Temp(0.0, 0.0), weather))
        val current = Current(1, 0.0, 0, 0, 0, 0, 0.0, weather, 0)

        val favorite = EntityFavorite(1, 0.0, 0.0, current, hourly, daily)
        val favorite2 = EntityFavorite(2, 0.0, 0.0, current, hourly, daily)
        database.getHomeWeather().insertFavorite(favorite)
        database.getHomeWeather().insertFavorite(favorite2)

        database.getHomeWeather().deleteFavorite(favorite)


        launch {
            database.getHomeWeather().getFavorite.collect {
                Assert.assertThat(it[0].id, IsEqual(2))
                cancel()
            }
        }
    }

    @Test
    fun getFavorite() = runBlockingTest {
        val weather = listOf(Weather("", "", ""))
        val hourly = listOf(Hourly(0, 0.0, weather))
        val daily = listOf(Daily(0, Temp(0.0, 0.0), weather))
        val current = Current(1, 0.0, 0, 0, 0, 0, 0.0, weather, 0)

        val favorite = EntityFavorite(1, 0.0, 0.0, current, hourly, daily)
        val favorite2 = EntityFavorite(2, 0.0, 0.0, current, hourly, daily)

        database.getHomeWeather().insertFavorite(favorite)
        database.getHomeWeather().insertFavorite(favorite2)



        launch {
            database.getHomeWeather().getFavorite.collect {
                Assert.assertThat(it[0].id, IsEqual(1))
                Assert.assertThat(it[1].id, IsEqual(2))
                cancel()
            }
        }
    }

    @Test
    fun insertAlert() = runBlockingTest {
        val weather = listOf(Weather("", "", ""))
        val alert = listOf(Alert("",0,",","",0, listOf()))
        val current = Current(1, 0.0, 0, 0, 0, 0, 0.0, weather, 0)

        val alert1 = EntityAlert("1",0.0,0.0,current,alert,"","","")

        database.getHomeWeather().insertAlert(alert1)

        launch {
            database.getHomeWeather().getAlert.collect {
                Assert.assertThat(it[0].id, IsEqual("1"))
                cancel()
            }
        }
    }

    @Test
    fun deleteAlert() = runBlockingTest {
        val weather = listOf(Weather("", "", ""))
        val alert = listOf(Alert("",0,",","",0, listOf()))
        val current = Current(1, 0.0, 0, 0, 0, 0, 0.0, weather, 0)

        val alert1 = EntityAlert("1",0.0,0.0,current,alert,"","","")
        val alert2 = EntityAlert("2",0.0,0.0,current,alert,"","","")


        database.getHomeWeather().insertAlert(alert1)
        database.getHomeWeather().insertAlert(alert2)
        database.getHomeWeather().deleteAlert(alert1)

        launch {
            database.getHomeWeather().getAlert.collect {
                Assert.assertThat(it[0].id, IsEqual("2"))
                cancel()
            }
        }
    }

    @Test
    fun getAlert() = runBlockingTest {

        val weather = listOf(Weather("", "", ""))
        val alert = listOf(Alert("", 0, ",", "", 0, listOf()))
        val current = Current(1, 0.0, 0, 0, 0, 0, 0.0, weather, 0)

        val alert1 = EntityAlert("1", 0.0, 0.0, current, alert, "", "", "")
        val alert2 = EntityAlert("2", 0.0, 0.0, current, alert, "", "", "")


        database.getHomeWeather().insertAlert(alert1)
        database.getHomeWeather().insertAlert(alert2)

        launch {
            database.getHomeWeather().getAlert.collect {
                Assert.assertThat(it[0].id, IsEqual("1"))
                Assert.assertThat(it[1].id, IsEqual("2"))
                cancel()
            }
        }
    }

    @Test
    fun getAlertById_EntityAlert() = runBlockingTest {
        val weather = listOf(Weather("", "", ""))
        val alert = listOf(Alert("", 0, ",", "", 0, listOf()))
        val current = Current(1, 0.0, 0, 0, 0, 0, 0.0, weather, 0)

        val alert1 = EntityAlert("1", 30.0, 0.0, current, alert, "", "", "")
        val alert2 = EntityAlert("2", 60.0, 0.0, current, alert, "", "", "")


        database.getHomeWeather().insertAlert(alert1)
        database.getHomeWeather().insertAlert(alert2)

        launch {
                Assert.assertThat(database.getHomeWeather().getAlertById("2").lat ,IsEqual(60.0))
                cancel()
            }
    }

}