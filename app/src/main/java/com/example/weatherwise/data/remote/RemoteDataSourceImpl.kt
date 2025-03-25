package com.example.weatherwise.data.remote

import com.example.weatherwise.data.models.WeatherResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class RemoteDataSourceImpl(private val service: RetroService) : RemoteDataSource {
    override suspend fun getWeather(lat: Double, lon: Double):Flow<WeatherResult> {

            return flowOf( service.getWeather(lat, lon).body()!!)

    }
}