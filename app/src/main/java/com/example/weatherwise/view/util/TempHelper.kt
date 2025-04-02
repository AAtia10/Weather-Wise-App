package com.example.weatherwise.view.util

import com.example.weatherwise.data.models.ForeCastResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getTemperatureUnit(setting: String): String {
    return when {
        setting.contains("°F", ignoreCase = true)|| setting.contains("°ف", ignoreCase = true) -> "imperial"
        setting.contains("°C", ignoreCase = true) || setting.contains("°م", ignoreCase = true) -> "metric"
        setting.contains("°K", ignoreCase = true) ||setting.contains("°ك", ignoreCase = true) -> "standard"
        setting.contains("meter/s", ignoreCase = true)  ||setting.contains("متر/ث", ignoreCase = true) -> "metric"
        setting.contains("mile/h", ignoreCase = true)|| setting.contains("ميل/س", ignoreCase = true) -> "imperial"
        else -> "Unknown Unit"
    }

}


fun getTemperatureDisplayUnit(apiUnit: String): String {
    return when (apiUnit) {
        "imperial" -> "°F"
        "metric" -> "°C"
        "standard" -> "°K"
        else -> "°C"
    }

}

fun getArabicTemperatureDisplayUnit(apiUnit: String): String {
    return when (apiUnit) {
        "imperial" -> "°ف"
        "metric" -> "°م"
        "standard" -> "°ك"
        else -> "°م"
    }
}


fun getWindDisplayUnit(apiUnit: String): String {
    return when (apiUnit) {
        "imperial" -> "mile/h"
        "metric" -> "meter/s"
        else -> "meter/s"
    }
}

fun getArabicWindUnit(apiUnit: String): String {
    return when (apiUnit) {
        "imperial" -> "ميل/س"
        "metric" -> "متر/ث"
        else -> "متر/ث"
    }
}


fun List<ForeCastResult.Item0>.getFirstForecastPerDay(): List<ForeCastResult.Item0> {
    return this.groupBy { it.dt_txt.substring(0, 10) } // Group by date (YYYY-MM-DD)
        .map { it.value.first() } // Take the first forecast of each day
}


fun Int.toDayOfWeek(): String {
    val date = Date(this.toLong() * 1000) // Convert seconds to milliseconds
    val format = SimpleDateFormat("EEE", Locale.getDefault()) // "Mon", "Tue", etc.
    return format.format(date)
}

fun Int.toDayMonth(): String {
    val date = Date(this.toLong() * 1000)
    val format = SimpleDateFormat("dd MMM", Locale.getDefault()) // "13 Feb", "05 Jan", etc.
    return format.format(date)
}
