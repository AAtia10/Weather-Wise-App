package com.example.weatherwise.view.favourite

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherwise.data.models.ForeCastResult
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.repo.WeatherRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import kotlin.test.Test


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class FavoritesViewModelTest {

    private lateinit var viewModel: FavoriteViewModel
    private val repo: WeatherRepositoryImpl = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var currentWeatherResponse: WeatherResult
    private lateinit var forecastResponse: ForeCastResult



    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FavoriteViewModel(repo)
        currentWeatherResponse = mockk(relaxed = true)
        forecastResponse = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getFavorite should emit Loading then Success with sorted locations`() = runTest {

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

        // Arrange

        coEvery { repo.getFavoritePlaces() } returns flowOf(listOf(weather1,weather2))

        // Act
        viewModel.getFavorites()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expected = listOf(weather1,weather2)


        assertEquals(
           expected,
            viewModel.favorites.value
        )
        coVerify { repo.getFavoritePlaces() }
    }

    @Test
    fun `addFavorite should add favorite place and call getFavorites`()
    {
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

        // Arrange
        coEvery { repo.addFavoritePlace(weather1) } returns Unit
        coEvery { repo.getFavoritePlaces() } returns flowOf(listOf(weather1))
        // Act
        viewModel.addFavorite(weather1)
        testDispatcher.scheduler.advanceUntilIdle()
        // Assert
        val expected = listOf(weather1)
        assertEquals(
            expected,
            viewModel.favorites.value
            )
        coVerify { repo.addFavoritePlace(weather1) }
        coVerify { repo.getFavoritePlaces() }

    }







}
