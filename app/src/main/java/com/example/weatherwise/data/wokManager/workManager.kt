package com.example.weatherwise.data.wokManager

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherwise.MainActivity
import com.example.weatherwise.R
import com.example.weatherwise.service.AlarmService

class WeatherAlarmWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {

        val serviceIntent = Intent(applicationContext, AlarmService::class.java).apply {
            putExtra("notification_duration", 10L)
        }
        ContextCompat.startForegroundService(applicationContext, serviceIntent)
        return Result.success()
    }





}





