package com.example.weatherwise.view.map

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherwise.R
import com.example.weatherwise.data.local.LocalDataSource
import com.example.weatherwise.data.local.WeatherDatabase
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence
import com.example.weatherwise.data.remote.RemoteDataSourceImpl
import com.example.weatherwise.data.remote.Retrofit
import com.example.weatherwise.data.repo.WeatherRepositoryImpl
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun MapScreen(isComeFromSettings: Boolean, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val mapViewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                RemoteDataSourceImpl(Retrofit.service),
                LocalDataSource.getInstance(
                    WeatherDatabase.getInstance(context).favoriteDao()
                ),
                SharedPrefrence.getInstance(context)
            )
        )
    )

    val insertedMsg by mapViewModel.inserted.collectAsStateWithLifecycle()
    val cityState = remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var markerPosition by remember { mutableStateOf(LatLng(31.12, 29.57)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 10f)
    }

    // State for search recommendations
    val recommendations by mapViewModel.recommendations.collectAsStateWithLifecycle()
    var showRecommendations by remember { mutableStateOf(false) }

    LaunchedEffect(insertedMsg) {
        insertedMsg?.let {
            if (it) {
                Toast.makeText(
                    context,
                    context.getString(R.string.location_inserted_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            mapViewModel.getLocationRecommendations(searchQuery, context)
            showRecommendations = true
        } else {
            showRecommendations = false
        }
    }

    LaunchedEffect(markerPosition) {
        val address = getAddressFromLocation(markerPosition, context)
        if (!address.isNullOrEmpty()) {
            cityState.value = address
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp))
            {
                // Search field with recommendations
                Box {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                        },
                        label = { Text(stringResource(R.string.search_location)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Recommendations dropdown
                    if (showRecommendations && recommendations.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp)
                                .heightIn(max = 200.dp),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(recommendations.size) { index ->
                                val recommendation = recommendations[index]
                                Text(
                                    text = recommendation,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            searchQuery = recommendation
                                            showRecommendations = false
                                            CoroutineScope(Dispatchers.Main).launch {
                                                try {
                                                    val latLng = withContext(Dispatchers.IO) {
                                                        getLatLngFromAddress(context, recommendation)
                                                    }
                                                    latLng?.let {
                                                        markerPosition = it
                                                        cameraPositionState.animate(
                                                            CameraUpdateFactory.newLatLngZoom(it, 10f)
                                                        )
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e("MapScreen", "Error animating camera: ${e.message}")
                                                }
                                            }
                                        }
                                        .padding(12.dp)
                                )
                                Divider()
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng ->
                            markerPosition = latLng
                        }
                    ) {
                        Marker(
                            state = MarkerState(position = markerPosition),
                            title = stringResource(R.string.selected_location),
                            snippet = "Lat: ${markerPosition.latitude}, Lng: ${markerPosition.longitude}"
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = cityState.value,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(4.dp)
                    )

                    Button(
                        onClick = {
                            val selectedAddress = cityState.value
                            val selectedLocation = markerPosition
                            if (isComeFromSettings) {
                                mapViewModel.saveMapLocation(selectedLocation)
                                onBackClick()
                            } else {
                                mapViewModel.saveLocation(
                                    selectedLocation.latitude,
                                    selectedLocation.longitude
                                )
                                onBackClick()
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.select_location))
                    }
                }
            }
}





private suspend fun getLatLngFromAddress(context: Context, address: String): LatLng? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val locations = geocoder.getFromLocationName(address, 1)
            if (!locations.isNullOrEmpty()) {
                val location = locations[0]
                LatLng(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MapScreen", "Geocoding failed: ${e.message}")
            null
        }
    }
}

private fun getAddressFromLocation(location: LatLng, context: Context): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val city = address.locality ?: ""
            val state = address.adminArea ?: ""
            val country = address.countryName ?: ""

            listOf(city, state, country)
                .filter { it.isNotEmpty() }
                .joinToString(", ")
        } else {
            context.getString(R.string.location_not_found)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        context.getString(R.string.error_retrieving_location_details)
    }
}




