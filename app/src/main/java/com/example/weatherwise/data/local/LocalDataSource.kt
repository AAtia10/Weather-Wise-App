package com.example.weatherwise.data.local

import com.example.weatherwise.data.models.WeatherResult
import kotlinx.coroutines.flow.Flow

class LocalDataSource private constructor(private val favoriteDao: FavoriteDao) {

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