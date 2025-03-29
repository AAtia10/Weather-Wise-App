package com.example.weatherwise.view.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherwise.R
import com.example.weatherwise.data.local.LocalDataSource
import com.example.weatherwise.data.local.WeatherDatabase
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence
import com.example.weatherwise.data.remote.RemoteDataSourceImpl
import com.example.weatherwise.data.remote.Retrofit
import com.example.weatherwise.data.repo.WeatherRepositoryImpl
import com.example.weatherwise.view.component.ActionBar
import com.example.weatherwise.view.component.AirQuality
import com.example.weatherwise.view.component.DailyForecast
import com.example.weatherwise.view.component.HourlyForecast
import com.example.weatherwise.view.component.WeeklyForecast
import com.example.weatherwise.view.util.getAirQualityList
import com.example.weatherwise.view.util.getHourlyForecastData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen() {
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeFactory(
            WeatherRepositoryImpl.getInstance(
                RemoteDataSourceImpl(Retrofit.service),
                LocalDataSource.getInstance(WeatherDatabase.getInstance(LocalContext.current).favoriteDao()),
                SharedPrefrence.getInstance(LocalContext.current)
            )
        )
    )

    val isConnected by homeViewModel.isConnected.collectAsStateWithLifecycle()
    val weather by homeViewModel.weatherData.collectAsStateWithLifecycle()
    val forecast by homeViewModel.forecastData.collectAsStateWithLifecycle()

    LaunchedEffect(isConnected) {
        if (isConnected) {
            homeViewModel.fetchWeather(31.12, 29.57)
            homeViewModel.fetchForecast(31.12, 29.57)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddings)
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {
            when {
                !isConnected -> NoInternetAnimation()
                weather == null || forecast == null -> LoadingAnimation()
                else -> {
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

@Composable
fun LoadingAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(composition)

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}


@Composable
fun NoInternetAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.internet))
    val progress by animateLottieCompositionAsState(composition)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}



fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val date = Date(timestamp * 1000)
    return sdf.format(date)
}