package com.example.weatherwise


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import com.example.weatherwise.ui.theme.WeatherWiseTheme
import com.example.weatherwise.view.splash.AppEntryPoint
import com.example.weatherwise.view.main.MainViewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mainViewModel.handleNotificationIntent(intent)


        mainViewModel.applyLanguage()

        setContent {
            WeatherWiseTheme {
                AppEntryPoint()
            }
        }
    }
}
