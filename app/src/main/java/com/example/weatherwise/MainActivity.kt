package com.example.weatherwise

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import com.example.weatherwise.data.SharedKeys
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence
import com.example.weatherwise.service.AlarmService
import com.example.weatherwise.ui.theme.WeatherWiseTheme
import com.example.weatherwise.view.splash.AppEntryPoint
import com.example.weatherwise.view.util.ConnectivityObserver
import com.example.weatherwise.view.util.getLanguage
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        intent?.let {
           if ( it.action=="CANCEL_NOTIFICATION")
           {
               val cancelIntet=Intent(this,AlarmService::class.java)
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   ContextCompat.startForegroundService(applicationContext, cancelIntet)
               } else {
               applicationContext.startService(cancelIntet)
           }
           }
        }
        ConnectivityObserver.initialize(this)
        super.onCreate(savedInstanceState)
        createNotificationChannel()
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Alerts"
            val descriptionText = "Weather notification channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("weather_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
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