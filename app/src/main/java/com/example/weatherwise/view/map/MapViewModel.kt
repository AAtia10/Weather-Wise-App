package com.example.weatherwise.view.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.repo.WeatherRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MapViewModel(private val repo: WeatherRepo) : ViewModel(){
    private val _inserted  = MutableStateFlow<Boolean?>(null)
    val inserted= _inserted.asStateFlow()

    fun saveLocation(lat:Double,lon:Double) {
        viewModelScope.launch {

            try {
                repo.fetchWeather(lat,lon,"metric","en").catch {

                    Log.i("TAG", "catch: ${it.message}")

                }
                    .collect{
                        repo.addFavoritePlace(it)
                        _inserted.emit(true)
                    }
            }
            catch (e:Exception)
            {
                Log.i("TAG", "catch: ${e.message}")

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
