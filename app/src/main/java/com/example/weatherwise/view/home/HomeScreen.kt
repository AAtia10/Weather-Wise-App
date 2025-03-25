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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherwise.data.remote.RemoteDataSourceImpl
import com.example.weatherwise.data.remote.Retrofit
import com.example.weatherwise.data.repo.WeatherRepositoryImpl
import com.example.weatherwise.view.component.ActionBar
import com.example.weatherwise.view.component.AirQuality
import com.example.weatherwise.view.component.DailyForecast
import com.example.weatherwise.view.component.WeeklyForecast
import com.example.weatherwise.view.util.getAirQualityList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen()
{

    val homeViewModel:HomeViewModel= viewModel(factory = HomeFactory(WeatherRepositoryImpl.getInstance(RemoteDataSourceImpl(Retrofit.service))))
    val weather by homeViewModel.weatherData.collectAsStateWithLifecycle()

    homeViewModel.fetchWeather(31.12,29.57)

    Scaffold(
        modifier = Modifier.fillMaxSize(),

    ) {
        paddings ->
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddings)
            .padding(
                horizontal = 24.dp,
                vertical = 10.dp
            )
        ) {
            Log.i("TAG", "HomeScreen: ${weather?.dt}")
           weather?.let {
               val feelsLike="${it.main.feels_like.toInt()} °C"
               val humidity="${it.main.humidity} %"
               val pressure="${it.main.pressure} hpa"
               val speed="${it.wind.speed} m/s"
               val sunrise = formatTime(it.sys.sunrise.toLong())
               val sunset = formatTime(it.sys.sunset.toLong())
               val airList= getAirQualityList(feelsLike,speed,pressure,humidity,sunrise,sunset)
               ActionBar(it)
               Spacer(modifier = Modifier.height(12.dp))
               DailyForecast(it)
               Spacer(modifier = Modifier.height(16.dp))
               AirQuality(data = airList)
               Spacer(modifier = Modifier.height(24.dp))
               WeeklyForecast()
               Spacer(modifier = Modifier.height(150.dp))

           }


        }

    }
}


fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // "hh:mm a" -> 12-hour format with AM/PM
    val date = Date(timestamp * 1000) // Convert seconds to milliseconds
    return sdf.format(date)
}