package com.example.weatherwise.data.repo

import com.example.weatherwise.data.local.LocalDataSource
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.remote.RemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertNotNull


class WeatherRepositoryImplTest()
{
    private lateinit var repoImpl: WeatherRepositoryImpl
    private lateinit var  remoteDataSourceImpl: RemoteDataSource
    private lateinit var localDataSourceImpl: LocalDataSource
    private lateinit var appPreference: SharedPrefrence
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
    fun setup() = runTest {
        localDataSourceImpl = mockk()
        remoteDataSourceImpl = mockk()
        appPreference = mockk()
        repoImpl = WeatherRepositoryImpl(remoteDataSourceImpl, localDataSourceImpl, appPreference)
    }

    @Test
    fun getFavoritesPlaces() = runTest {
        coEvery { localDataSourceImpl.getFavorites() } returns flowOf(listOf(

            weather1,
            weather2
        ))
        val locations = repoImpl.getFavoritePlaces().firstOrNull()
        assertNotNull(locations)
        assertThat(locations.size, `is`(2))
        assertThat(locations[0].name, `is`("Cairo"))
        assertThat(locations[1].name, `is`("London"))
        coVerify { localDataSourceImpl.getFavorites() }
    }

    @Test
    fun getFavoritePlaceById() = runTest {
        coEvery { localDataSourceImpl.getFavoriteById(1) } returns flowOf(weather1)
        val location = repoImpl.getFavoritePlacesbyId(1).firstOrNull()
        assertNotNull(location)
        assertThat(location.name, `is`("Cairo"))
        coVerify { localDataSourceImpl.getFavoriteById(1) }

    }
    @Test
    fun addFavoritePlace() = runTest {
        coEvery { localDataSourceImpl.addFavorite(weather1) } returns Unit
        repoImpl.addFavoritePlace(weather1)
        coVerify { localDataSourceImpl.addFavorite(weather1) }
    }



}