package com.example.weatherwise.view.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherwise.R
import com.example.weatherwise.data.SharedKeys
import com.example.weatherwise.data.local.LocalDataSource
import com.example.weatherwise.data.local.WeatherDatabase
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence
import com.example.weatherwise.data.remote.RemoteDataSourceImpl
import com.example.weatherwise.data.remote.Retrofit
import com.example.weatherwise.data.repo.WeatherRepositoryImpl
import com.example.weatherwise.view.home.HomeFactory
import com.example.weatherwise.view.util.getArabicTemperatureDisplayUnit
import com.example.weatherwise.view.util.getArabicWindUnit
import com.example.weatherwise.view.util.getGpsLocation
import com.example.weatherwise.view.util.getTemperatureDisplayUnit
import com.example.weatherwise.view.util.getTemperatureUnit
import com.example.weatherwise.view.util.getWindDisplayUnit
import com.example.weatherwise.view.util.restartActivity

@Composable
fun SettingsScreen(onMapClicked:()->Unit) {
    val context = LocalContext.current
    val viewModel:SettingsViewModel=  viewModel(
        factory = SettingsFactory(
            WeatherRepositoryImpl.getInstance(
                RemoteDataSourceImpl(Retrofit.service),
                LocalDataSource.getInstance(WeatherDatabase.getInstance(context).favoriteDao()),
                SharedPrefrence.getInstance(context)
            )
        )
    )

    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    val permissionState = remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    )}

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionState.value = isGranted
        if (isGranted) {
            if (isLocationEnabled(context))
            {
                context.getGpsLocation { viewModel.saveGpsLocation(it) }
            }
            else
            {
                openLocationSettings(context)
            }

        } else {
            Toast.makeText(context, "Location Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    var selectedLanguage by remember { mutableStateOf(viewModel.fetchData(SharedKeys.LANGUAGE.toString(),"English")) }
    var selectedTemp by remember {
        mutableStateOf(
            when (selectedLanguage) {
                "العربية" -> getArabicTemperatureDisplayUnit(
                    viewModel.fetchData(SharedKeys.DEGREE.toString(), "°م")
                )
                else -> getTemperatureDisplayUnit(
                    viewModel.fetchData(SharedKeys.DEGREE.toString(), "°C")
                )
            }
        )
    }

    var selectedWindSpeed by remember {
        mutableStateOf(
            when (selectedLanguage) {
                "العربية" -> getArabicWindUnit(
                    viewModel.fetchData(SharedKeys.SPEED_UNIT.toString(), "متر/ث")
                )
                else -> getWindDisplayUnit(
                    viewModel.fetchData(SharedKeys.SPEED_UNIT.toString(), "meter/s")
                )
            }
        )
    }
    Log.i("WTAG", "aboveSettingsScreen: $selectedWindSpeed")


    var selectedLocation by remember {mutableStateOf(viewModel.fetchData(SharedKeys.LOCATION.toString(),"Map")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Temperature Selector
        SettingToggle(stringResource(R.string.temperature), listOf(stringResource(R.string.c),
            stringResource(
                R.string.f
            ), stringResource(R.string.k)
        ), selectedTemp,viewModel) {
            selectedTemp = it

            if (selectedTemp == context.getString(R.string.c)||selectedTemp == context.getString(R.string.k))
            {
                selectedWindSpeed=context.getString(R.string.meter_s)

            }
            else
            {
                selectedWindSpeed=context.getString(R.string.mile_h)

            }


            viewModel.saveData(SharedKeys.DEGREE.toString(), getTemperatureUnit(it))
            viewModel.saveData(SharedKeys.SPEED_UNIT.toString(), getTemperatureUnit(it))
        }

        Spacer(modifier = Modifier.height(32.dp))


        SettingToggle(stringResource(R.string.wind_speed), listOf(stringResource(R.string.meter_s),
            stringResource(
                R.string.mile_h
            )
        ), selectedWindSpeed,viewModel) {
            selectedWindSpeed = it

            if (selectedWindSpeed == context.getString(R.string.meter_s))
            {
                selectedTemp=context.getString(R.string.c)

            }

            else
            {
                selectedTemp=context.getString(R.string.f)

            }



            viewModel.saveData(SharedKeys.SPEED_UNIT.toString(), getTemperatureUnit(it))
            viewModel.saveData(SharedKeys.DEGREE.toString(), getTemperatureUnit(it))
            Log.i("WTAG", "SettingsScreen: ${getTemperatureUnit(it)}")
        }

        Spacer(modifier = Modifier.height(32.dp))


        SettingToggle(stringResource(R.string.language), listOf(stringResource(R.string.english),
            stringResource(
                R.string.arabic
            )
        ), selectedLanguage,viewModel) {
            selectedLanguage = it
            viewModel.saveData(SharedKeys.LANGUAGE.toString(),it)
            Log.i("TAG", "SettingsScreen:$selectedLanguage ")
            restartActivity(context)
        }

        Spacer(modifier = Modifier.height(32.dp))


        SettingToggle(stringResource(R.string.n), listOf(stringResource(R.string.gps), stringResource(R.string.map)), selectedLocation,viewModel) {
            selectedLocation = it
            viewModel.saveData(SharedKeys.LOCATION.toString(),it)
            if (it=="Map"||it=="الخريطة")
            {
                onMapClicked()
            }
            else
            {
                if (permissionState.value)
                {
                    if (!isLocationEnabled(context))
                    {
                        openLocationSettings(context)
                    }
                    else
                    {
                        context.getGpsLocation { viewModel.saveGpsLocation(it) }
                    }
                }
                else{
                    launcher.launch(permission)
                }

            }
        }
    }
}

@Composable
fun SettingToggle(title: String, options: List<String>, selected: String,viewModel: SettingsViewModel, onSelected: (String) -> Unit) {
    Column {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            options.forEach { option ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (selected == option) Color(0xFF2196F3) else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(vertical = 12.dp)
                        .clickable {
                            onSelected(option)

                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected == option) Color.White else Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

private fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}
private fun openLocationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}
