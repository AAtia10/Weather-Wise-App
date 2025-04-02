package com.example.weatherwise.data.local

import android.content.Context
import androidx.room.*
import com.example.weatherwise.data.models.AlarmConverter
import com.example.weatherwise.data.models.AlarmEntity
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.models.dto.HomeForecastData
import com.example.weatherwise.data.models.dto.HomeWeatherData
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(place: WeatherResult)

    @Query("SELECT * FROM weather_table")
    fun getAllFavorites(): Flow<List<WeatherResult>>

    @Query("SELECT * FROM weather_table WHERE id = :id")
    fun getFavoriteById(id: Int): Flow<WeatherResult>

    @Delete
    suspend fun deleteFavorite(place: WeatherResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(place: AlarmEntity)

    @Query("SELECT * FROM alarms")
    fun getAllAlarm(): Flow<List<AlarmEntity>>

    @Delete
    suspend fun deleteAlarm(place: AlarmEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeWeather(place: HomeWeatherData)

    @Query("SELECT * FROM home_weather_data Where id=0")
    fun getHomeWeather(): Flow <HomeWeatherData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeForecast(place: HomeForecastData)

    @Query("SELECT * FROM home_forecast_data Where id=0")
    fun getHomeForecast(): Flow <HomeForecastData>
}

@Database(entities = [WeatherResult::class,AlarmEntity::class,HomeWeatherData::class,HomeForecastData::class], version = 13, exportSchema = false)
@TypeConverters(AlarmConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"

                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}