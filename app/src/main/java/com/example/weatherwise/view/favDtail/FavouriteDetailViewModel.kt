package com.example.weatherwise.view.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.data.models.ForeCastResult
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.repo.WeatherRepo
import com.example.weatherwise.view.util.ConnectivityObserver
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavouriteDetailViewModel(private val repo: WeatherRepo) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _weatherData = MutableStateFlow<WeatherResult?>(null)
    val weatherData = _weatherData.asStateFlow()

    private val _forecastData = MutableStateFlow<ForeCastResult?>(null)
    val forecastData = _forecastData.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun loadWeatherData(weatherId: Int) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                repo.getFavoritePlacesbyId(weatherId)
                    .catch { e ->
                        _errorMessage.value = "Failed to load saved location: ${e.message}"
                        _isLoading.value = false
                    }
                    .collect { weather ->
                        if (weather != null) {
                            _weatherData.value = weather
                            fetchForecast(weather.coord.lat, weather.coord.lon)
                        } else {
                            _errorMessage.value = "Location data not found"
                            _isLoading.value = false
                        }
                    }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading data: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun fetchForecast(lat: Double, lon: Double) {
        if (!ConnectivityObserver.isConnected.value) {
            _errorMessage.value = "No internet connection - showing cached data"
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                repo.fetchForecast(
                    lat,
                    lon,
                    repo.fetchData("DEGREE", "Celsius"),
                    repo.fetchData("LANGUAGE", "en")
                )
                    .catch { e ->
                        _errorMessage.value = "Failed to load forecast: ${e.message}"
                    }
                    .collect { forecast ->
                        _forecastData.value = forecast
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching forecast: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    class FavouriteDetailFactory(private val repo: WeatherRepo) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavouriteDetailViewModel::class.java)) {
                return FavouriteDetailViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}