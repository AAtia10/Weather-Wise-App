package com.example.weatherwise.view.home


import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.data.SharedKeys
import com.example.weatherwise.data.models.ForeCastResult
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.models.dto.HomeForecastData
import com.example.weatherwise.data.models.dto.HomeWeatherData
import com.example.weatherwise.data.repo.WeatherRepo
import com.example.weatherwise.view.util.ConnectivityObserver
import com.example.weatherwise.view.util.getLanguage
import com.example.weatherwise.view.util.getTemperatureUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class HomeViewModel(private val repo: WeatherRepo) : ViewModel() {

    val isConnected: StateFlow<Boolean> = ConnectivityObserver.isConnected

    private val _language = MutableStateFlow("")
    val language: StateFlow<String> = _language
    private val _temperatureUnit = MutableStateFlow("")
    val temperatureUnit: StateFlow<String> = _temperatureUnit.asStateFlow()

    init {
        _language.value = getLanguage(repo.fetchData(SharedKeys.LANGUAGE.toString(), "en"))
        _temperatureUnit.value = repo.fetchData(
            SharedKeys.DEGREE.toString(), getTemperatureUnit("°C")
        )
        Log.i("TAG", "Homeviewmodel:${_language.value} ")
    }

    private val _weatherData = MutableStateFlow<WeatherResult?>(null)
    val weatherData = _weatherData.asStateFlow()

    private val _forecastData = MutableStateFlow<ForeCastResult?>(null)
    val forecastData: StateFlow<ForeCastResult?> = _forecastData.asStateFlow()

    private val _msg = MutableStateFlow<String>("")
    val msg = _msg.asStateFlow()


    fun fetchWeather(
        lat: Double = repo.fetchData("Maplat", 31.12),
        lon: Double = repo.fetchData("Maplog", 29.57),

        ) {

        if (isConnected.value) {
            viewModelScope.launch {
                try {
                    repo.fetchWeather(lat, lon, _temperatureUnit.value, _language.value)
                        .catch {
                            _msg.emit(it.message ?: "Error")
                            Log.i("TAG", "catch: ${it.message}")

                        }
                        .collect {
                            _weatherData.emit(it)
                            insertHomeWeather(HomeWeatherData(weatherResult = it))
                            Log.i("TAG", "view: ${it.dt}")
                        }
                } catch (e: Exception) {
                    _msg.emit(e.message ?: "Error")
                    Log.i("TAG", "try: ${e.message}")
                }

            }
        }
    }


    fun fetchForecast(
        lat: Double = repo.fetchData("Maplat", 31.12),
        lon: Double = repo.fetchData("Maplog", 29.57),
    ) {

        if (isConnected.value) {
            viewModelScope.launch {
                repo.fetchForecast(lat, lon, _temperatureUnit.value, _language.value)
                    .catch {
                        _msg.emit(it.message ?: "Error fetching forecast")
                        Log.e("HomeViewModel", "Error fetching forecast: ${it.message}")
                    }
                    .collect {
                        _forecastData.emit(it)
                        insertHomeForecast(HomeForecastData(forecastResult = it))
                        Log.i("HomeViewModel", "Forecast Data Fetched: ${it.list.size} items")
                    }
            }
        }
    }

    suspend fun insertHomeWeather(weather: HomeWeatherData) {
        repo.insertHomeWeather(weather)
    }
    suspend fun insertHomeForecast(forecast: HomeForecastData) {
        repo.insertHomeForecast(forecast)
    }

    fun fetchLocalWeather(
        ) {
        viewModelScope.launch {
            repo.getHomeWeather().collect {

                _weatherData.emit(it.weatherResult)
            }

        }
    }


    fun fetchLocalForecast(
    ) {
        viewModelScope.launch {
            repo.getHomeForecast().collect {

                _forecastData.emit(it.forecastResult)
            }
        }


    }
}

class HomeFactory(private val repo: WeatherRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
