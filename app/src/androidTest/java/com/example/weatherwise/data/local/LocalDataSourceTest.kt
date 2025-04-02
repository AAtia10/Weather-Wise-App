package com.example.weatherwise.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherwise.data.models.ForeCastResult
import com.example.weatherwise.data.models.WeatherResult
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest

class LocalDataSourceTest()
{
    private lateinit var localDataSourceImpl: LocalDataSource
    private lateinit var weatherDao: FavoriteDao
    private lateinit var db: WeatherDatabase
    private lateinit var currentWeatherResponse: WeatherResult
    private lateinit var forecast: ForeCastResult

    val weather1 = WeatherResult(
        id = 1,
        name = "Cairo",
        base = "m",
        clouds = WeatherResult.Clouds(all = 10),
        cod = 200,
        coord = WeatherResult.Coord(lat = 30.0, lon = 31.0),
        dt = 1680300000,
        main = WeatherResult.Main(
            temp = 25.0, temp_min = 20.0, temp_max = 30.0,
            feels_like = 26.0, pressure = 1012, humidity = 50,
            sea_level = 1012, grnd_level = 1000
        ),
        sys = WeatherResult.Sys(country = "EG", sunrise = 1680250000, sunset = 1680300000),
        timezone = 7200,
        visibility = 10000,
        weather = listOf(),
        wind = WeatherResult.Wind(deg = 180, gust = 10.0, speed = 5.0)
    )

    val weather2 = WeatherResult(
        id = 2,
        name = "London",
        base = "m",
        clouds = WeatherResult.Clouds(all = 90),
        cod = 200,
        coord = WeatherResult.Coord(lat = 51.5, lon = -0.12),
        dt = 1680300000,
        main = WeatherResult.Main(
            temp = 15.0, temp_min = 10.0, temp_max = 18.0,
            feels_like = 14.0, pressure = 1015, humidity = 80,
            sea_level = 1015, grnd_level = 1005
        ),
        sys = WeatherResult.Sys(country = "GB", sunrise = 1680250000, sunset = 1680300000),
        timezone = 0,
        visibility = 8000,
        weather = listOf(),
        wind = WeatherResult.Wind(deg = 200, gust = 15.0, speed = 7.0)
    )


    @Before
    fun setup(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()


        weatherDao = db.favoriteDao()
        localDataSourceImpl = LocalDataSource(weatherDao)
        currentWeatherResponse = mockk(relaxed = true)
        forecast = mockk(relaxed = true)


    }
    @After
    fun tearDown(){
        db.close()
    }

    @Test
    fun getAllFavorites()= runTest {

        localDataSourceImpl.addFavorite(weather1)
        localDataSourceImpl.addFavorite(weather2)
        val favorites = localDataSourceImpl.getFavorites().firstOrNull()
        assertThat(favorites, `is`(listOf(weather1, weather2)))

    }
    @Test
    fun getFavoriteById()= runTest {
        localDataSourceImpl.addFavorite(weather1)
        localDataSourceImpl.addFavorite(weather2)
        val favorite = localDataSourceImpl.getFavoriteById(2).firstOrNull()
        assertThat(favorite, `is`(weather2))
    }
    @Test
    fun deleteFavorite()= runTest {
        localDataSourceImpl.addFavorite(weather1)
        localDataSourceImpl.addFavorite(weather2)
        localDataSourceImpl.deleteFavorite(weather2)
        val favorites = localDataSourceImpl.getFavorites().firstOrNull()
        assertThat(favorites, `is`(listOf(weather1)))


    }




}