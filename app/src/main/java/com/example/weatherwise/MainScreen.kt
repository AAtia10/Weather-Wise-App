package com.example.weatherwise


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navListItem = listOf(
        NavItem(stringResource(R.string.home), Icons.Default.Home),
        NavItem(stringResource(R.string.alert), Icons.Default.Notifications),
        NavItem(stringResource(R.string.favourite), Icons.Default.Favorite),
        NavItem(stringResource(R.string.settings), Icons.Default.Settings)
    )

    val navController = rememberNavController()

    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        bottomBar = {
            NavigationBar {
                navListItem.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            navController.popBackStack()
                            when (selectedIndex) {
                                0 -> navController.navigate(ScreenRoutes.HomeScreen)
                                1 ->navController.navigate(ScreenRoutes.AlertScreen)
                                2 -> navController.navigate(ScreenRoutes.FavouriteScreen)
                                3 -> navController.navigate(ScreenRoutes.SettingsScreen)
                            }
                                  },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = "icon")
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding))
        {
            SetupNavHost(navController = navController)
        }
    }
}