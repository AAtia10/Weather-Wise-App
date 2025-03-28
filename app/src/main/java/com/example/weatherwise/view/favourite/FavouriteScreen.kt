package com.example.weatherwise.view.favourite

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherwise.R
import com.example.weatherwise.data.local.LocalDataSource
import com.example.weatherwise.data.local.WeatherDatabase
import com.example.weatherwise.data.local.sharedPrefrence.SharedPrefrence
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.data.remote.RemoteDataSourceImpl
import com.example.weatherwise.data.remote.Retrofit
import com.example.weatherwise.data.repo.WeatherRepositoryImpl
import com.example.weatherwise.view.util.formatNumberBasedOnLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun FavouriteScreen(
    onAddClick: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(
            WeatherRepositoryImpl
                .getInstance(
                    RemoteDataSourceImpl(Retrofit.service),
                    LocalDataSource.getInstance(WeatherDatabase.getInstance(context).favoriteDao()),
                    SharedPrefrence.getInstance(context)
                )
        )
    )
    val favouriteList by favoriteViewModel.favorites.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        favoriteViewModel.getFavorites()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddClick() },
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.location)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay
    ) { innerPadding ->
        if (favouriteList.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                FavoritePlacesList(
                    favoriteList = favouriteList,
                    onRemove = { place ->
                        coroutineScope.launch {
                            favoriteViewModel.deleteFavoriteWithUndo(place, snackbarHostState)
                        }
                    }
                )
            }
        } else {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fav))
            val progress by animateLottieCompositionAsState(composition)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(composition = composition, progress = { progress })
            }
        }
    }
}


@Composable
fun FavoritePlaceCard(place: WeatherResult) {
    val weatherIcon = getWeatherIcon(place)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = weatherIcon),
                contentDescription = "Weather Icon",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text =  "${formatNumberBasedOnLanguage(place.main.temp.toInt().toString())}°C",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@Composable
fun FavoritePlacesList(favoriteList: List<WeatherResult>, onRemove: (WeatherResult) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(favoriteList, key = { it.id }) { place ->
            SwipeToDismissBox(
                item = place,
                onRemove = { onRemove(place) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissBox(
    item: WeatherResult,
    onRemove: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val swipeToDismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            if (state == SwipeToDismissBoxValue.EndToStart) {
                coroutineScope.launch {
                    delay(1000) // Wait for 1 second before removing
                    onRemove()
                }
                true
            } else {
                false
            }
        }
    )

    val backgroundColor by animateColorAsState(
        targetValue = when (swipeToDismissState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> Color.Green
            SwipeToDismissBoxValue.EndToStart -> Color.Red
            else -> Color.White
        }, label = "Animate bg color"
    )

    SwipeToDismiss(
        state = swipeToDismissState,
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )
        }, dismissContent = {FavoritePlaceCard(item)}
    )

}


@Composable
fun getWeatherIcon(weather: WeatherResult?): Int {
    return when (weather?.weather?.firstOrNull()?.main) {
        "Clear" -> R.drawable.img_sun
        "Clouds" -> R.drawable.img_cloudy
        "Rain" -> R.drawable.img_rain
        "Snow" -> R.drawable.img_snow
        else -> R.drawable.img_sub_rain // Default fallback icon
    }
}


