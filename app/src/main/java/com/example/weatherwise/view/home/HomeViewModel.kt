package com.example.weatherwise.view.home



import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.repo.WeatherRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class HomeViewModel(private val repo: WeatherRepo) : ViewModel() {

    // Exposes weather data as a StateFlow for UI observation
    private val _weatherData  = MutableStateFlow<WeatherResult?>(null)
    val weatherData= _weatherData.asStateFlow()
   private val _msg=MutableStateFlow<String>("")
    val msg=_msg.asStateFlow()



    // Fetch weather data based on latitude & longitude
    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                repo.fetchWeather(lat, lon)
                    .catch {
                        _msg.emit(it.message?:"Error")
                        Log.i("TAG", "catch: ${it.message}")

                }
                    .collect{
                        _weatherData.emit(it)
                        Log.i("TAG", "view: ${it.dt}")
                    }
            }
            catch (e:Exception)
            {
                _msg.emit(e.message?:"Error")
                Log.i("TAG", "try: ${e.message}")
            }

        }
    }
}

class HomeFactory(private val repo: WeatherRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}