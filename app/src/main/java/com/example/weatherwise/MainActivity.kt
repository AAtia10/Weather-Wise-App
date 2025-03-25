package com.example.weatherwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weatherwise.ui.theme.WeatherWiseTheme
import com.example.weatherwise.view.splash.AppEntryPoint


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherWiseTheme {

                AppEntryPoint()


            }
        }
    }
}