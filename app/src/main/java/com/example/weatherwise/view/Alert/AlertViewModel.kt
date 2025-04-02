package com.example.weatherwise.view.Alert

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherwise.data.models.AlarmEntity
import com.example.weatherwise.data.repo.WeatherRepo
import com.example.weatherwise.data.wokManager.WeatherAlarmWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AlertViewModel (private val repo: WeatherRepo) : ViewModel()
{
   private val _uiState= MutableStateFlow<List<AlarmEntity>>(emptyList())
    val uiState=_uiState.asStateFlow()

    fun insertAlarm(alarmEntity: AlarmEntity)
    {
        viewModelScope.launch {
            repo.insertAlarm(alarmEntity)

        }
    }

    fun deleteAlarm(alarmEntity: AlarmEntity)
    {
        viewModelScope.launch {
            repo.deleteAlarm(alarmEntity)

        }
    }
    fun getAlarm()
    {
        try {
            viewModelScope.launch {
                repo.getAllAlarm().catch {  Log.i("TAG", "getAlarm:${it.message} ") }
                  .map { list ->
                       list.filter { it.time > System.currentTimeMillis() }
                   }
                    .collect{
                    _uiState.emit(it)

                }


            }
        }
        catch (e:Exception)
        {
            Log.i("TAG", "getAlarm:${e.message} ")
        }

    }



    fun scheduleWeatherAlarm(notificationTimeInMillis: Long, notificationDurationSeconds: Long, context: Context) {
        val delay = notificationTimeInMillis - System.currentTimeMillis()

        val inputData = workDataOf("notification_duration" to notificationDurationSeconds)

        val workRequest = OneTimeWorkRequestBuilder<WeatherAlarmWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS) // Schedule for the selected time
            .setInputData(inputData) // Pass notification duration
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)

        Toast.makeText(context, "Notification scheduled!", Toast.LENGTH_SHORT).show()
    }
}
class AlertViewModelFactory(private val repo: WeatherRepo) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            return AlertViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}