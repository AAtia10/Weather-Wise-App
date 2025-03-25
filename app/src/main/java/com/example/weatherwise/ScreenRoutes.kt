package com.example.weatherwise

import kotlinx.serialization.Serializable


sealed class ScreenRoutes(){

    @Serializable
    object SplashScreen:ScreenRoutes()

    @Serializable
    object HomeScreen:ScreenRoutes()

    @Serializable
    object FavouriteScreen:ScreenRoutes()

    @Serializable
    object SettingsScreen:ScreenRoutes()

    @Serializable
    object AlertScreen:ScreenRoutes()

    @Serializable
    object MapScreen:ScreenRoutes()




}