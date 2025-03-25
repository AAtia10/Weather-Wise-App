package com.example.weatherwise

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.weatherwise.view.Alert.AlertScreen
import com.example.weatherwise.view.favourite.FavouriteScreen
import com.example.weatherwise.view.home.HomeScreen
import com.example.weatherwise.view.map.MapScreen
import com.example.weatherwise.view.settings.SettingsScreen

@Composable
fun SetupNavHost(  navController:NavHostController)
{
    NavHost(navController = navController,
        startDestination = ScreenRoutes.HomeScreen
        )
    {
        composable<ScreenRoutes.HomeScreen>{
            HomeScreen()
        }
        composable<ScreenRoutes.SettingsScreen>{
            SettingsScreen()
        }
        composable<ScreenRoutes.FavouriteScreen>{
            FavouriteScreen(){
                navController.navigate(ScreenRoutes.MapScreen)
            }
        }
        composable<ScreenRoutes.AlertScreen>{
            AlertScreen()
        }
        composable<ScreenRoutes.MapScreen>{
            MapScreen()
        }




}



    }

