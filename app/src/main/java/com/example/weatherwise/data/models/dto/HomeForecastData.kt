package com.example.weatherwise.data.models.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherwise.data.models.Converters
import com.example.weatherwise.data.models.ForeCastResult



@TypeConverters(Converters::class)
@Entity(tableName = "home_forecast_data")
data class HomeForecastData(
    @PrimaryKey val id: Int=0,
    val forecastResult:ForeCastResult
)
