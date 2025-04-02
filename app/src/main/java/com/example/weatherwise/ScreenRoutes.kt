package com.example.weatherwise

import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.serialization.Serializable

sealed class ScreenRoutes {

    @Serializable
    object SplashScreen : ScreenRoutes()

    @Serializable
    object HomeScreen : ScreenRoutes()

    @Serializable
    object FavouriteScreen : ScreenRoutes()

    @Serializable
    object SettingsScreen : ScreenRoutes()

    @Serializable
    object AlertScreen : ScreenRoutes()

    @Serializable
    data class MapScreen(val isComeFromSettings:Boolean) : ScreenRoutes()

    @Serializable
    object FavouriteDetailScreen : ScreenRoutes() {

        const val ROUTE = "favourite_detail/{weatherId}"


        const val ARG_WEATHER_ID = "weatherId"


        val arguments = listOf(
            navArgument(ARG_WEATHER_ID) {
                type = NavType.IntType
            }
        )

        fun createRoute(weatherId: Int) = "favourite_detail/$weatherId"
    }
}