package com.example.weatherwise.view.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherwise.R
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.ui.theme.ColorGradient1
import com.example.weatherwise.ui.theme.ColorGradient2
import com.example.weatherwise.ui.theme.ColorGradient3
import com.example.weatherwise.ui.theme.ColorTextSecondary

@Composable
fun ActionBar(
   weather:WeatherResult

    ){



    Log.i("TAG", "ActionBar: ${weather?.dt}")
    Row(modifier=Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
        ) {

        LocationInfo(weather=weather)


    }
}



@Composable
private fun LocationInfo(
    modifier: Modifier = Modifier,
    weather: WeatherResult?
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_location_pin),
                contentDescription = null,
                modifier = Modifier.height(18.dp),
                contentScale = ContentScale.FillHeight
            )
            Text(
                text = weather?.name ?: "Loading...",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )
        }
        ProgressBar(weather=weather)
    }
}

@Composable
private fun ProgressBar(
    modifier: Modifier = Modifier,
    weather: WeatherResult?
) {
    val temperature = weather?.main?.temp?.toInt() ?: 0
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    0f to ColorGradient1,
                    0.25f to ColorGradient2,
                    1f to ColorGradient3
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                vertical = 2.dp,
                horizontal = 10.dp
            )
    ) {
        Text(
            text = "Temp: $temperature°C",
            style = MaterialTheme.typography.labelSmall,
            color = ColorTextSecondary.copy(alpha = 0.7f)
        )
    }
}