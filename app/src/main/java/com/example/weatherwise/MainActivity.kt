package com.example.weatherwise

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weatherwise.data.SharedKeys
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence
import com.example.weatherwise.ui.theme.WeatherWiseTheme
import com.example.weatherwise.view.splash.AppEntryPoint
import com.example.weatherwise.view.util.ConnectivityObserver
import com.example.weatherwise.view.util.getLanguage
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ConnectivityObserver.initialize(this)
        super.onCreate(savedInstanceState)
        val sharedPref = SharedPrefrence.getInstance(this)
        val lang = getLanguage(
            sharedPref.fetchData(
                SharedKeys.LANGUAGE.toString(),
                Locale.getDefault().language
            )
        )
        Log.i("TAG", "Main:$lang ")
        applyLanguage(lang)
        setContent {
            WeatherWiseTheme {

                AppEntryPoint()


            }
        }
    }


    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}