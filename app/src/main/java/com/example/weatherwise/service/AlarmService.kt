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
import com.example.weatherwise.data.repo.WeatherRepo

class AlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var repo:WeatherRepo

    override fun onBind(intent: Intent): IBinder?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "CANCEL_NOTIFICATION") {
          stopSelf()
            stopForeground(STOP_FOREGROUND_REMOVE)
            return START_NOT_STICKY
        }

        val weatherInfo = fetchWeatherData() // Fetch latest weather data
        val notificationDuration = intent?.getLongExtra("notification_duration", 10L)?:10L // Default: 10 seconds
        sendNotification(weatherInfo, notificationDuration)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        return super.onStartCommand(intent, flags, startId)
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
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, "weather_channel")
            .setContentTitle("Weather Update")
            .setContentText(weatherInfo)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.wind)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.sunset, "Dismiss", cancelPendingIntent) // Cancel button
            .build()

        startForeground(1,notification)


    }


    private fun fetchWeatherData(): String {
        return "It's sunny, 25°C" // Replace with actual API call or Room database fetch
    }


}