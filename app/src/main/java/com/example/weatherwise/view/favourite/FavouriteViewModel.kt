package com.example.weatherwise.view.favourite

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherwise.data.local.LocalDataSource
import com.example.weatherwise.data.models.WeatherResult

import com.example.weatherwise.data.repo.WeatherRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repo: WeatherRepo) : ViewModel() {

    private val _favorites = MutableStateFlow<List<WeatherResult>>(emptyList())
    val favorites = _favorites.asStateFlow()

    init {
        getFavorites()
    }

     fun getFavorites() {
        viewModelScope.launch {
            repo.getFavoritePlaces().collect { places ->
                _favorites.value = places
            }
        }
    }

    fun addFavorite(place: WeatherResult) {
        viewModelScope.launch {
            repo.addFavoritePlace(place)
            getFavorites()
        }
    }

    fun deleteFavoriteWithUndo(place: WeatherResult, snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {

            _favorites.value = _favorites.value - place
            repo.deleteFavoritePlace(place) // Remove from database immediately

            val result = snackbarHostState.showSnackbar(
                message = "${place.name} removed",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {

                addFavorite(place)
            }
        }
    }
}

class FavoriteViewModelFactory(private val repo: WeatherRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteViewModel(repo) as T
    }
}