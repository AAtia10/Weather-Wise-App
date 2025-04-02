package com.example.weatherwise.view.map

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.data.SharedKeys
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.repo.WeatherRepo
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapViewModel(private val repo: WeatherRepo) : ViewModel() {
    private val _inserted = MutableStateFlow<Boolean?>(null)
    val inserted = _inserted.asStateFlow()

    private val _recommendations = MutableStateFlow<List<String>>(emptyList())
    val recommendations = _recommendations.asStateFlow()

    private var searchJob: Job? = null

    fun saveLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                repo.fetchWeather(lat, lon, "metric", "en")
                    .catch { Log.i("TAG", "catch: ${it.message}") }
                    .collect {
                        repo.addFavoritePlace(it)
                        _inserted.emit(true)
                    }
            } catch (e: Exception) {
                Log.i("TAG", "catch: ${e.message}")
            }
        }
    }

    fun saveMapLocation(latLng: LatLng) {
        repo.saveData("Maplat", latLng.latitude)
        repo.saveData("Maplog", latLng.longitude)
    }

    fun getLocationRecommendations(query: String, context: Context) {
        searchJob?.cancel() // Cancel previous search if any

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(query, 5) ?: emptyList()

                val suggestions = addresses.mapNotNull { address ->
                    val city = address.locality ?: ""
                    val state = address.adminArea ?: ""
                    val country = address.countryName ?: ""

                    listOf(city, state, country)
                        .filter { it.isNotEmpty() }
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(", ")
                }

                _recommendations.emit(suggestions)
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error getting recommendations: ${e.message}")
                _recommendations.emit(emptyList())
            }
        }
    }

    fun searchForLocation(query: String, context: Context, onResult: (LatLng?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(query, 1)

                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    val latLng = LatLng(location.latitude, location.longitude)
                    withContext(Dispatchers.Main) {
                        onResult(latLng)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult(null)
                    }
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error searching location: ${e.message}")
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }
}

class MapViewModelFactory(private val repo: WeatherRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}