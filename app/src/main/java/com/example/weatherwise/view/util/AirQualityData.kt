package com.example.weatherwise.view.util


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.weatherwise.R

data class AirQualityItem(
    @DrawableRes val icon: Int,
    @StringRes val title: Int, // Now using string resource ID
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
            title = R.string.real_feel,
            value = feelsLike,
            icon = R.drawable.ic_real_feel
        ),
        AirQualityItem(
            title = R.string.sun_set,
            value = sunset,
            icon = R.drawable.sunset,
        ),
        AirQualityItem(
            title = R.string.sun_rise,
            value = sunrise,
            icon = R.drawable.sunrise
        ),
        AirQualityItem(
            title = R.string.wind,
            value = windSpeed,
            icon = R.drawable.wind
        ),
        AirQualityItem(
            title = R.string.humidity,
            value = humditiy,
            icon = R.drawable.hum
        ),
        AirQualityItem(
            title = R.string.pressure,
            value = pressure,
            icon = R.drawable.pressure
        )
    )
}