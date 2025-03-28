package com.example.weatherwise.data.repo

import com.example.weatherwise.data.models.ForeCastResult
import com.example.weatherwise.data.models.WeatherResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WeatherRepo {

    suspend fun fetchWeather(lat: Double, lon: Double,units: String ,lang: String):Flow<WeatherResult>
    suspend fun fetchForecast(lat: Double, lon: Double,units: String ,lang: String): Flow<ForeCastResult>
    suspend fun addFavoritePlace(place: WeatherResult)
    suspend fun deleteFavoritePlace(place: WeatherResult)
    suspend fun getFavoritePlaces(): Flow<List<WeatherResult>>
    suspend fun getFavoritePlacesbyId(id:Int): Flow<WeatherResult>
     fun <T> saveData(key: String, value: T)
     fun <T> fetchData(key: String, defaultValue: T): T

}