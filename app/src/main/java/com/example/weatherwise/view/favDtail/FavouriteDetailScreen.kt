package com.example.weatherwise.view.forecast

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherwise.data.local.LocalDataSource
import com.example.weatherwise.data.local.WeatherDatabase
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence
import com.example.weatherwise.data.models.ForeCastResult
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.remote.RemoteDataSourceImpl
import com.example.weatherwise.data.remote.Retrofit
import com.example.weatherwise.data.repo.WeatherRepo
import com.example.weatherwise.data.repo.WeatherRepositoryImpl
import com.example.weatherwise.view.component.*
import com.example.weatherwise.view.home.formatTime
import com.example.weatherwise.view.util.getAirQualityList
import com.example.weatherwise.view.util.getHourlyForecastData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteDetailScreen(
    navController: NavController,
    weatherId: Int  // Changed to Int to match your repository
) {
    val context = LocalContext.current
    val viewModel: FavouriteDetailViewModel = viewModel(
        factory = FavouriteDetailViewModel.FavouriteDetailFactory(
            WeatherRepositoryImpl.getInstance(
                RemoteDataSourceImpl(Retrofit.service),
                LocalDataSource.getInstance(
                    WeatherDatabase.getInstance(LocalContext.current).favoriteDao()
                ),
                SharedPrefrence.getInstance(LocalContext.current)
            )
        )
    )

    val weather by viewModel.weatherData.collectAsStateWithLifecycle()
    val forecast by viewModel.forecastData.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(weatherId) {
        viewModel.loadWeatherData(weatherId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(weather?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = errorMessage ?: "Error loading data")
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    weather?.let { weather ->
                        val feelsLike = "${weather.main.feels_like.toInt()} °C"
                        val humidity = "${weather.main.humidity} %"
                        val pressure = "${weather.main.pressure} hpa"
                        val speed = "${weather.wind.speed} m/s"
                        val sunrise = formatTime(weather.sys.sunrise.toLong())
                        val sunset = formatTime(weather.sys.sunset.toLong())

                        val airList = getAirQualityList(feelsLike, speed, pressure, humidity, sunrise, sunset)

                        ActionBar(weather)
                        Spacer(modifier = Modifier.height(12.dp))
                        DailyForecast(weather)
                        Spacer(modifier = Modifier.height(16.dp))
                        AirQuality(data = airList)
                        Spacer(modifier = Modifier.height(24.dp))

                        forecast?.let {
                            val list = it.list.subList(0, 8)
                            HourlyForecast(list = getHourlyForecastData(list))
                            Spacer(modifier = Modifier.height(24.dp))
                            WeeklyForecast()
                            Spacer(modifier = Modifier.height(150.dp))
                        }
                    }
                }
            }
        }
    }
}

// ViewModel for ForecastDetailScreen
class ForecastDetailViewModel(private val repo: WeatherRepo) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _weatherData = MutableStateFlow<WeatherResult?>(null)
    val weatherData = _weatherData.asStateFlow()

    private val _forecastData = MutableStateFlow<ForeCastResult?>(null)
    val forecastData: StateFlow<ForeCastResult?> = _forecastData.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadWeatherData(weatherId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repo.getFavoritePlacesbyId(weatherId).collect { weather ->
                    _weatherData.value = weather
                    weather?.let {
                        fetchForecast(it.coord.lat, it.coord.lon)
                    } ?: run {
                        _errorMessage.value = "Weather data not found"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading weather: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchForecast(lat: Double, lon: Double) {
        try {
            repo.fetchForecast(
                lat,
                lon,
                repo.fetchData("DEGREE", "Celsius"),
                repo.fetchData("LANGUAGE", "en")
            ).collect { forecast ->
                _forecastData.value = forecast
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error loading forecast: ${e.message}"
        }
    }
}

class ForecastDetailViewModelFactory(private val repo: WeatherRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForecastDetailViewModel::class.java)) {
            return ForecastDetailViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}