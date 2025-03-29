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
    object MapScreen : ScreenRoutes()

    @Serializable
    object FavouriteDetailScreen : ScreenRoutes() {
        // Define route pattern with argument
        const val ROUTE = "favourite_detail/{weatherId}"

        // Define argument name
        const val ARG_WEATHER_ID = "weatherId"

        // Create arguments configuration
        val arguments = listOf(
            navArgument(ARG_WEATHER_ID) {
                type = NavType.IntType
            }
        )

        // Helper function to build route with argument
        fun createRoute(weatherId: Int) = "favourite_detail/$weatherId"
    }
}