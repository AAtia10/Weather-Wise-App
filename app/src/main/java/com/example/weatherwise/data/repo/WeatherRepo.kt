package com.example.weatherwise.data.repo

import com.example.weatherwise.data.models.AlarmEntity
import com.example.weatherwise.data.models.ForeCastResult
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.models.dto.HomeForecastData
import com.example.weatherwise.data.models.dto.HomeWeatherData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WeatherRepo {

    suspend fun fetchWeather(lat: Double, lon: Double,units: String ,lang: String):Flow<WeatherResult>
    suspend fun fetchForecast(lat: Double, lon: Double,units: String ,lang: String): Flow<ForeCastResult>
    suspend fun addFavoritePlace(place: WeatherResult)
    suspend fun deleteFavoritePlace(place: WeatherResult)
    suspend fun getFavoritePlaces(): Flow<List<WeatherResult>>
    suspend fun getFavoritePlacesbyId(id:Int): Flow<WeatherResult>
    suspend fun insertHomeWeather(place: HomeWeatherData)
    suspend fun insertHomeForecast(place: HomeForecastData)
    fun getHomeWeather():Flow<HomeWeatherData>
    fun getHomeForecast():Flow<HomeForecastData>
    suspend fun deleteAlarm(place: AlarmEntity)
    fun getAllAlarm(): Flow<List<AlarmEntity>>
    suspend fun insertAlarm(place: AlarmEntity)
     fun <T> saveData(key: String, value: T)
     fun <T> fetchData(key: String, defaultValue: T): T

}