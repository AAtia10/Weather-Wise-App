package com.example.weatherwise.data.repo

import com.example.weatherwise.data.remote.RemoteDataSource
import com.example.weatherwise.data.models.WeatherResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource
) : WeatherRepo {

    override suspend fun fetchWeather(lat: Double, lon: Double):Flow<WeatherResult> {
        return remoteDataSource.getWeather(lat,lon)
    }

    companion object {
        @Volatile
        private var INSTANCE: WeatherRepositoryImpl? = null

        fun getInstance(remoteDataSource: RemoteDataSource): WeatherRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherRepositoryImpl(remoteDataSource)
                INSTANCE = instance
                instance
            }
        }
    }
}