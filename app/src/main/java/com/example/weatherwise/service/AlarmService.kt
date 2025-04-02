package com.example.weatherwise.service


import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.weatherwise.MainActivity
import com.example.weatherwise.R
import com.example.weatherwise.data.SharedKeys
import com.example.weatherwise.data.local.LocalDataSource
import com.example.weatherwise.data.local.WeatherDatabase
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence
import com.example.weatherwise.data.remote.RemoteDataSourceImpl
import com.example.weatherwise.data.remote.Retrofit
import com.example.weatherwise.data.repo.WeatherRepo
import com.example.weatherwise.data.repo.WeatherRepositoryImpl
import com.example.weatherwise.view.util.formatNumberBasedOnLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private  var repo = WeatherRepositoryImpl.getInstance(
        RemoteDataSourceImpl(Retrofit.service),
        LocalDataSource.getInstance(WeatherDatabase.getInstance(this).favoriteDao()),
        SharedPrefrence.getInstance(this)
    )

    override fun onBind(intent: Intent): IBinder?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "CANCEL_NOTIFICATION") {
          stopSelf()
            stopForeground(STOP_FOREGROUND_REMOVE)
            return START_NOT_STICKY
        }
        val notificationDuration = intent?.getLongExtra("notification_duration", 10L) ?: 10L // Default: 10 sec

        CoroutineScope(Dispatchers.IO).launch {
            val weatherInfo = fetchWeatherData() // 🔹 Now fetches actual weather data before sending notification
            launch(Dispatchers.Main) {
                sendNotification(weatherInfo, notificationDuration)
                mediaPlayer.isLooping = true
                mediaPlayer.start()
            }
        }

        return START_STICKY
    }


    override fun onCreate() {
        super.onCreate()
        Log.i("TAG", "onCreate: ")
        mediaPlayer = MediaPlayer.create(this, R.raw.song)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(weatherInfo: String, duration: Long) {
        val context = applicationContext


        // Intent to cancel the notification
        val cancelIntent = Intent(this, AlarmService::class.java).apply {
            action = "CANCEL_NOTIFICATION"
        }

        val cancelPendingIntent = PendingIntent.getService(
            this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the notification with a cancel button
        val intent = Intent(context, AlarmService::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, "weather_channel")
            .setContentTitle(getString(R.string.weather_update))
            .setContentText(weatherInfo)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.wind)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.sunset, "Dismiss", cancelPendingIntent) // Cancel button
            .build()

        startForeground(1,notification)


    }


    private suspend fun fetchWeatherData(): String {
        return try {
            val lat = repo.fetchData("Maplat", 31.12) // Default: 31.12
            val lon = repo.fetchData("Maplog", 29.57) // Default: 29.57
            val tempUnit = repo.fetchData(SharedKeys.DEGREE.toString(), "metric")
            val lang = repo.fetchData(SharedKeys.LANGUAGE.toString(), "en")

            val weatherResult = repo.fetchWeather(lat, lon, tempUnit, lang).first()

           "${weatherResult.weather[0].description},  ${formatNumberBasedOnLanguage(weatherResult.main.temp.toInt().toString())}°C"
        } catch (e: Exception) {
            Log.e("AlarmService", "Error fetching weather: ${e.message}")
            "Weather data unavailable"
        }
    }


}