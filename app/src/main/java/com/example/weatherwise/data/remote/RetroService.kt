package com.example.weatherwise.data.remote

import com.example.weatherwise.data.models.ForeCastResult
import com.example.weatherwise.data.models.WeatherResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetroService {

    @GET("data/2.5/weather")
       suspend fun getWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("units") units: String = "metric",
            @Query("lang") lang: String = "en",
            @Query("appid") apiKey: String="78ff4401b504f3a53ee5c00a776ca794"
        ):Response<WeatherResult>


    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = "78ff4401b504f3a53ee5c00a776ca794"
    ): Response<ForeCastResult>
}
