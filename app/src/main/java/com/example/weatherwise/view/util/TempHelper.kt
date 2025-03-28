package com.example.weatherwise.view.util

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
