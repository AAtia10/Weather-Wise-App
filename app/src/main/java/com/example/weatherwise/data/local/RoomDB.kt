package com.example.weatherwise.data.local

import android.content.Context
import androidx.room.*
import com.example.weatherwise.data.models.WeatherResult
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
}

@Database(entities = [WeatherResult::class], version = 10, exportSchema = false)
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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}