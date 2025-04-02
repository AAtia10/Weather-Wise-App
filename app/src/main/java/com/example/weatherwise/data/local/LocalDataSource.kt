package com.example.weatherwise.data.local

import com.example.weatherwise.data.models.AlarmEntity
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.models.dto.HomeForecastData
import com.example.weatherwise.data.models.dto.HomeWeatherData
import kotlinx.coroutines.flow.Flow

class LocalDataSource (private val favoriteDao: FavoriteDao) {

    suspend fun addFavorite(place: WeatherResult) {
        favoriteDao.insertFavorite(place)
    }

    fun getFavorites(): Flow<List<WeatherResult>> {
        return favoriteDao.getAllFavorites()


    }
    fun getFavoriteById(id: Int): Flow<WeatherResult>{
        return favoriteDao.getFavoriteById(id)
    }

    suspend fun deleteFavorite(place: WeatherResult) {
        favoriteDao.deleteFavorite(place)
    }


    suspend fun insertAlarm(place: AlarmEntity)
    {
        favoriteDao.insertAlarm(place)
    }


    fun getAllAlarm(): Flow<List<AlarmEntity>>
    {
       return favoriteDao.getAllAlarm()
    }


    suspend fun deleteAlarm(place: AlarmEntity)
    {
        favoriteDao.deleteAlarm(place)
    }

    suspend fun insertHomeWeather(place:HomeWeatherData)=favoriteDao.insertHomeWeather(place)

     fun getHomeWeather()=favoriteDao.getHomeWeather()

    suspend fun insertHomeForecast(place:HomeForecastData)=favoriteDao.insertHomeForecast(place)
    fun getHomeForecast()=favoriteDao.getHomeForecast()



    companion object {
        private var instance: LocalDataSource? = null
        fun getInstance(favoriteDao: FavoriteDao): LocalDataSource {
            return instance ?: synchronized(this) {
                val newInstance = LocalDataSource(favoriteDao)
                instance = newInstance
                newInstance
            }
        }
    }
}