package com.example.weatherwise.view.util

import com.example.weatherwise.R
import com.example.weatherwise.data.models.ForeCastResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getHourlyForecastData(list:List<ForeCastResult.Item0>) : List<ForecastItem>{
    val resultList : MutableList<ForecastItem> = mutableListOf()

    list.forEach {
        resultList.add(
            ForecastItem(
                image = getWeatherIcon(it.weather.firstOrNull()?.main),
                dayOfWeek = formatHourTime(it.dt.toLong()),
                date = "",
                temperature = "${it.main.temp.toInt()}°",
                airQuality = "Good",
                airQualityIndicatorColorHex = "#2dbe8d"
            )
        )

    }

    return resultList
}


fun getWeatherIcon(condition: String?): Int {
    return when (condition) {
        "Clear" -> R.drawable.img_sun
        "Clouds" -> R.drawable.img_cloudy
        "Rain" -> R.drawable.img_rain
        "Snow" -> R.drawable.img_snow
        "Thunderstorm" -> R.drawable.img_thunder
        "Drizzle" -> R.drawable.img_sub_rain
        else -> R.drawable.img_cloudy // Default icon
    }
}

fun formatHourTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh a", Locale.getDefault())
    val date = Date(timestamp * 1000)
    return sdf.format(date)
}

