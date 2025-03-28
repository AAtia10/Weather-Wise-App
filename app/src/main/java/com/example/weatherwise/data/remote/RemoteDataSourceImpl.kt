package com.example.weatherwise.data.remote

import com.example.weatherwise.data.models.ForeCastResult
import com.example.weatherwise.data.models.WeatherResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class RemoteDataSourceImpl(private val service: RetroService) : RemoteDataSource {
    override suspend fun getWeather(lat: Double, lon: Double, units: String, lang: String):Flow<WeatherResult> {

            return flowOf( service.getWeather(lat, lon,units ,lang).body()!!)

    }

    override suspend fun getForeCast(lat: Double, lon: Double,units: String ,lang: String ): Flow<ForeCastResult> {
        return flowOf(service.getForecast(lat,lon,units,lang).body()!!)
    }
}