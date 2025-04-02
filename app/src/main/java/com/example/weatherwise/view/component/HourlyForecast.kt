package com.example.weatherwise.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherwise.R
import com.example.weatherwise.ui.theme.ColorGradient1
import com.example.weatherwise.ui.theme.ColorGradient2
import com.example.weatherwise.ui.theme.ColorTextPrimary
import com.example.weatherwise.view.util.ForecastItem
import com.example.weatherwise.view.util.formatNumberBasedOnLanguage

@Composable
fun HourlyForecast(modifier: Modifier = Modifier
                   ,list: List<ForecastItem>
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.hourly_forecast),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextPrimary
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp) // Add end padding to prevent clipping
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Reduce spacing
            ) {
                items(
                    items = list,
                    key = { it.dayOfWeek }
                ) { item ->
                    ForecastItemCard(item)
                }
            }
        }
    }
}

@Composable
private fun ForecastItemCard(

    item: ForecastItem
) {
    Column(
        modifier = Modifier
            .width(75.dp) // Increase width to avoid clipping
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(ColorGradient1, ColorGradient2)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.dayOfWeek,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(item.image),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = formatNumberBasedOnLanguage (item.temperature),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}
