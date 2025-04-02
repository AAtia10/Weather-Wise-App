package com.example.weatherwise

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weatherwise.view.Alert.AlertScreen
import com.example.weatherwise.view.favourite.FavouriteScreen
import com.example.weatherwise.view.forecast.FavouriteDetailScreen
import com.example.weatherwise.view.home.HomeScreen
import com.example.weatherwise.view.map.MapScreen
import com.example.weatherwise.view.settings.SettingsScreen

@Composable
fun SetupNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.HomeScreen
    ) {
        composable<ScreenRoutes.HomeScreen> {
            HomeScreen()
        }
        composable<ScreenRoutes.SettingsScreen> {
            SettingsScreen(){
                navController.navigate(ScreenRoutes.MapScreen(true))
            }
        }
        composable<ScreenRoutes.FavouriteScreen> {
            FavouriteScreen(
                navController = navController,
                onAddClick = {
                    navController.navigate(ScreenRoutes.MapScreen(false))
                }
            )
        }
        composable<ScreenRoutes.AlertScreen> {
            AlertScreen()
        }
        composable<ScreenRoutes.MapScreen> {
            val args:ScreenRoutes.MapScreen=it.toRoute()
            val value=args.isComeFromSettings
            MapScreen(value) {
                navController.popBackStack()

            }
        }
        composable(
            route = ScreenRoutes.FavouriteDetailScreen.ROUTE,
            arguments = ScreenRoutes.FavouriteDetailScreen.arguments
        ) { backStackEntry ->
            val weatherId = backStackEntry.arguments?.getInt(
                ScreenRoutes.FavouriteDetailScreen.ARG_WEATHER_ID
            ) ?: -1
            FavouriteDetailScreen(
                navController = navController,
                weatherId = weatherId
            )
        }
    }
}