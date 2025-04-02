package com.example.weatherwise.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@TypeConverters(Converters::class)
@Entity(tableName = "weather_table")

data class WeatherResult(
    @PrimaryKey val id: Int,
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
)
{
    data class Clouds(
        val all: Int
    )

    data class Coord(
        val lat: Double,
        val lon: Double
    )

    data class Main(
        val feels_like: Double,
        val grnd_level: Int,
        val humidity: Int,
        val pressure: Int,
        val sea_level: Int,
        val temp: Double,
        val temp_max: Double,
        val temp_min: Double
    )

    data class Sys(
        val country: String,
        val sunrise: Int,
        val sunset: Int
    )

    data class Weather(
        val description: String,
        val icon: String,
        val id: Int,
        val main: String
    )

    data class Wind(
        val deg: Int,
        val gust: Double,
        val speed: Double
    )
}
class Converters {

    @TypeConverter
    fun fromClouds(clouds: WeatherResult.Clouds): String = Gson().toJson(clouds)

    @TypeConverter
    fun toClouds(cloudsString: String): WeatherResult.Clouds =
        Gson().fromJson(cloudsString, WeatherResult.Clouds::class.java)

    @TypeConverter
    fun fromCoord(coord: WeatherResult.Coord): String = Gson().toJson(coord)

    @TypeConverter
    fun toCoord(coordString: String): WeatherResult.Coord =
        Gson().fromJson(coordString, WeatherResult.Coord::class.java)

    @TypeConverter
    fun fromMain(main: WeatherResult.Main): String = Gson().toJson(main)

    @TypeConverter
    fun toMain(mainString: String): WeatherResult.Main =
        Gson().fromJson(mainString, WeatherResult.Main::class.java)

    @TypeConverter
    fun fromSys(sys: WeatherResult.Sys): String = Gson().toJson(sys)

    @TypeConverter
    fun toSys(sysString: String): WeatherResult.Sys =
        Gson().fromJson(sysString, WeatherResult.Sys::class.java)

    @TypeConverter
    fun fromWeatherList(weather: List<WeatherResult.Weather>): String = Gson().toJson(weather)

    @TypeConverter
    fun toWeatherList(weatherString: String): List<WeatherResult.Weather> {
        val listType = object : TypeToken<List<WeatherResult.Weather>>() {}.type
        return Gson().fromJson(weatherString, listType)
    }

    @TypeConverter
    fun fromWind(wind: WeatherResult.Wind): String = Gson().toJson(wind)

    @TypeConverter
    fun toWind(windString: String): WeatherResult.Wind =
        Gson().fromJson(windString, WeatherResult.Wind::class.java)

    @TypeConverter
    fun fromWeatherResult(weatherResult: WeatherResult?): String {
        return Gson().toJson(weatherResult)
    }

    @TypeConverter
    fun toWeatherResult(data: String): WeatherResult {
        val type = object : TypeToken<WeatherResult>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun fromForeCastResult(foreCastResult: ForeCastResult?): String {
        return Gson().toJson(foreCastResult)
    }

    @TypeConverter
    fun toForeCastResult(data: String): ForeCastResult {
        val type = object : TypeToken<ForeCastResult>() {}.type
        return Gson().fromJson(data, type)
    }
}
