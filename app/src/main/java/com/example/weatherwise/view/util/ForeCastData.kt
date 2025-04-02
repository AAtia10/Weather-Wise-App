package com.example.weatherwise.view.util

import androidx.annotation.DrawableRes
import com.example.weatherwise.R
import com.example.weatherwise.data.models.ForeCastResult

data class ForecastItem(
    @DrawableRes val image: Int,
    val dayOfWeek: String,
    val date: String,
    val temperature: String,
    val airQuality: String,
    val airQualityIndicatorColorHex: String,

)

fun getForecastData(list:List<ForeCastResult.Item0>) : List<ForecastItem>{
    val resultList : MutableList<ForecastItem> = mutableListOf()

    list.forEach {
        resultList.add(
            ForecastItem(
                image = getWeatherIcon(it.weather.firstOrNull()?.main),
                dayOfWeek =it.dt.toDayOfWeek() ,
                date = it.dt.toDayMonth(),
                temperature = formatNumberBasedOnLanguage("${it.main.temp.toInt()}°"),
                airQuality = formatNumberBasedOnLanguage("${it.main.humidity} %"),
                airQualityIndicatorColorHex = "#ff7676"
            )
        )

    }

    return resultList
}


