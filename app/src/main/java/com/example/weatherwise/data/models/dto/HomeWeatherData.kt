package com.example.weatherwise.data.models.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherwise.data.models.Converters
import com.example.weatherwise.data.models.WeatherResult

@TypeConverters(Converters::class)
@Entity(tableName = "home_weather_data")
data class HomeWeatherData(
    @PrimaryKey val id: Int=0,
   val weatherResult: WeatherResult
)
