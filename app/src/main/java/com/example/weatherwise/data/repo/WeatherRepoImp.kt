package com.example.weatherwise.data.repo

import com.example.weatherwise.data.models.ForeCastResult
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.remote.RemoteDataSource
import com.example.weatherwise.data.local.LocalDataSource
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence

import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val sharedP:SharedPrefrence
) : WeatherRepo {

    override suspend fun fetchWeather(lat: Double, lon: Double,units: String ,lang: String): Flow<WeatherResult> {
        return remoteDataSource.getWeather(lat, lon,units,lang)
    }

    override suspend fun fetchForecast(lat: Double, lon: Double,units: String ,lang: String): Flow<ForeCastResult> {
        return remoteDataSource.getForeCast(lat, lon,units,lang)
    }


    override suspend fun addFavoritePlace(place: WeatherResult) {
        localDataSource.addFavorite(place)
    }

    override suspend fun deleteFavoritePlace(place: WeatherResult) {
        localDataSource.deleteFavorite(place)
    }

    override suspend fun getFavoritePlaces(): Flow<List<WeatherResult>> {
        return localDataSource.getFavorites()
    }

    override suspend fun getFavoritePlacesbyId(id: Int): Flow<WeatherResult> {
        return localDataSource.getFavoriteById(id)
    }

    override fun <T> saveData(key: String, value: T) {
        sharedP.saveData(key,value)
    }

    override fun <T> fetchData(key: String, defaultValue: T): T {
        return sharedP.fetchData(key,defaultValue)
    }

    companion object {
        @Volatile
        private var INSTANCE: WeatherRepositoryImpl? = null

        fun getInstance(remoteDataSource: RemoteDataSource, localDataSource: LocalDataSource,sharedP:SharedPrefrence): WeatherRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherRepositoryImpl(remoteDataSource, localDataSource,sharedP)
                INSTANCE = instance
                instance
            }
        }
    }
}
