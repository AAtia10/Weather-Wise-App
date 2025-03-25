package com.example.weatherwise.data.repo

import com.example.weatherwise.data.models.WeatherResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WeatherRepo {

    suspend fun fetchWeather(lat: Double, lon: Double):Flow<WeatherResult>
}