package com.example.weatherwise.view.main

import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.data.SharedKeys
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence
import com.example.weatherwise.service.AlarmService
import com.example.weatherwise.view.util.ConnectivityObserver
import com.example.weatherwise.view.util.getLanguage
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    init {
        // Initialize the connectivity observer
        ConnectivityObserver.initialize(application)
    }

    // Function to handle language setting logic
    fun applyLanguage() {
        val sharedPref = SharedPrefrence.getInstance(getApplication())
        val lang = getLanguage(
            sharedPref.fetchData(
                SharedKeys.LANGUAGE.toString(),
                Locale.getDefault().language
            )
        )
        Log.i("TAG", "Main:$lang ")
        setLanguage(lang)
    }

    // Function to set the language based on the provided code
    private fun setLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = getApplication<Application>().resources.configuration
        config.setLocale(locale)
        getApplication<Application>().resources.updateConfiguration(config, getApplication<Application>().resources.displayMetrics)
    }

    // Function to handle notification cancellation
    fun handleNotificationIntent(intent: Intent?) {
        intent?.let {
            if (it.action == "CANCEL_NOTIFICATION") {
                val cancelIntent = Intent(getApplication(), AlarmService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getApplication<Application>().startForegroundService(cancelIntent)
                } else {
                    getApplication<Application>().startService(cancelIntent)
                }
            }
        }
    }
}
