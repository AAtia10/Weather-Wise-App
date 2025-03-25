package com.example.weatherwise.view.util


import androidx.annotation.DrawableRes
import com.example.weatherwise.R

data class AirQualityItem(
    @DrawableRes val icon: Int,
    val title: String,
    val value: String
)





fun getAirQualityList(
    feelsLike:String,
    windSpeed:String,
    pressure:String,
    humditiy:String,
    sunrise:String,
    sunset:String

):List<AirQualityItem>{

    return  listOf(
        AirQualityItem(
            title = "Real Feel",
            value = feelsLike,
            icon = R.drawable.ic_real_feel
        ),
        AirQualityItem(
            title = "Sun Set",
            value = sunset,
            icon = R.drawable.ic_wind_quality,
        ),
        AirQualityItem(
            title = "Sun Rise",
            value = sunrise,
            icon = R.drawable.baseline_visibility_24
        ),
        AirQualityItem(
            title = "Wind",
            value = windSpeed,
            icon = R.drawable.ic_rain_chance
        ),
        AirQualityItem(
            title = "humidity",
            value = humditiy,
            icon = R.drawable.ic_uv_index
        ),
        AirQualityItem(
            title = "pressure",
            value = pressure,
            icon = R.drawable.ic_o3
        )
    )
}