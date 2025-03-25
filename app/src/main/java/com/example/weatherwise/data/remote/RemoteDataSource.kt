package com.example.weatherwise.data.remote

import com.example.weatherwise.data.models.WeatherResult
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {

    suspend fun getWeather(lat: Double, lon: Double):Flow<WeatherResult>
}