package com.example.weatherwise.view.settings

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherwise.data.repo.WeatherRepo
import com.example.weatherwise.view.home.HomeViewModel
import com.google.android.gms.maps.model.LatLng

class SettingsViewModel (private val repo: WeatherRepo) : ViewModel() {

    fun <T> saveData(key: String, value: T)
    {
        repo.saveData(key,value)
    }


    fun <T> fetchData(key: String, defaultValue: T): T
    {
        return repo.fetchData(key,defaultValue)
    }

    fun saveGpsLocation(location: Location){
        repo.saveData("Maplat",location.latitude)
        repo.saveData("Maplog",location.longitude)

    }

}



class SettingsFactory(private val repo: WeatherRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

