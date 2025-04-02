package com.example.weatherwise.view.Alert

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkManager
import com.example.weatherwise.R
import com.example.weatherwise.data.local.LocalDataSource
import com.example.weatherwise.data.local.WeatherDatabase
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence
import com.example.weatherwise.data.models.AlarmEntity
import com.example.weatherwise.data.remote.RemoteDataSourceImpl
import com.example.weatherwise.data.remote.Retrofit
import com.example.weatherwise.data.repo.WeatherRepositoryImpl
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import com.example.weatherwise.ui.theme.ColorGradient3
import java.time.Instant
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlertScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val alertViewModel: AlertViewModel = viewModel(
        factory = AlertViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                RemoteDataSourceImpl(Retrofit.service),
                LocalDataSource.getInstance(WeatherDatabase.getInstance(LocalContext.current).favoriteDao()),
                SharedPrefrence.getInstance(LocalContext.current)
            )
        )
    )
    alertViewModel.getAlarm()

    var showDialog by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }
    val alarms by alertViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(alarms) {
        Log.i("TAG", "AlertScreen: ${alarms.size}")
    }

    Box(modifier = modifier.fillMaxSize()) {
        PrintAlarms(alarmEntity = alarms) {
            alertViewModel.deleteAlarm(it)
            WorkManager.getInstance(context).cancelAllWorkByTag(it.id)
            
        }
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor =  MaterialTheme.colorScheme.primary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Alarm", tint = Color.White)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Set Alarm Duration") },
            text = {
                Column {
                    Button(onClick = {
                        showTimePicker(context) { hour, minute ->
                            startTime = LocalTime.of(hour, minute)
                        }
                    },colors = ButtonDefaults.buttonColors(containerColor = ColorGradient3)) {
                        Text("Select Start Time")
                    }
                    startTime?.let {
                        Text("Start Time: ${it.hour}:${it.minute}")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        showTimePicker(context) { hour, minute ->
                            endTime = LocalTime.of(hour, minute)
                        }
                    },colors = ButtonDefaults.buttonColors(containerColor = ColorGradient3)) {
                        Text("Select End Time")
                    }
                    endTime?.let {
                        Text("End Time: ${it.hour}:${it.minute}")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (startTime != null && endTime != null) {
                            val nowMillis = System.currentTimeMillis() // Get current time in milliseconds
                            val startMillis = startTime!!.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
                            val endMillis = endTime!!.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
                            val duration = endMillis - startMillis

                            if (startMillis <= nowMillis) {
                                Toast.makeText(context, "Time must be in the future", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.i("TAG", "AlertScreen: $startMillis+${startMillis.toInt()}")
                                alertViewModel.insertAlarm(
                                    AlarmEntity(
                                        System.currentTimeMillis().toString(),
                                        "Weather Wise: Alarm is Created",
                                        startMillis,
                                        duration
                                    )
                                )
                                alertViewModel.scheduleWeatherAlarm(startMillis, duration, context)

                                showDialog = false
                            }
                        } else {
                            Toast.makeText(context, "Please select both start and end times", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("Save", color = Color.White)
                }
            },

            dismissButton = {
                Button(onClick = { showDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Cancel", color = Color.White)
                }
            }
        )

    }


}

fun showTimePicker(context: Context, onTimeSelected: (Int, Int) -> Unit) {
    TimePickerDialog(
        context,
        { _, hourOfDay, minute -> onTimeSelected(hourOfDay, minute) },
        0,
        0,
        true
    ).show()
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrintAlarms(alarmEntity: List<AlarmEntity>, onDelete: (AlarmEntity) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(alarmEntity) { alarm ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
                shape = RoundedCornerShape(16.dp), // Smoother rounded edges
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Alarm Set",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatDateTime(alarm.time),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { onDelete(alarm) },
                        modifier = Modifier.size(40.dp) // Larger touch target
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.deletee), // Replace with your icon
                            contentDescription = "Delete Alarm",
                            modifier = Modifier.size(28.dp) // Increased size for better visibility
                        )
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun formatDateTime(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy • hh:mm a")
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

