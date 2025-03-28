package com.example.weatherwise.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.weatherwise.R
import com.example.weatherwise.data.models.WeatherResult
import com.example.weatherwise.ui.theme.ColorGradient1
import com.example.weatherwise.ui.theme.ColorGradient2
import com.example.weatherwise.ui.theme.ColorGradient3
import com.example.weatherwise.ui.theme.ColorTextSecondary
import com.example.weatherwise.ui.theme.ColorTextSecondaryVariant
import com.example.weatherwise.ui.theme.ColorWindForecast
import com.example.weatherwise.view.util.formatNumberBasedOnLanguage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyForecast(
    weather: WeatherResult?
){
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val(forecastImage,forecastValue,windImage,title,description,background)=createRefs()

        cardBackground(
            modifier = Modifier.constrainAs(background){
                linkTo(
                    start = parent.start,
                    end=parent.end,
                    top=parent.top,
                    bottom = description.bottom,
                    topMargin = 24.dp
                )
                height=Dimension.fillToConstraints
            }
        )
        val weatherIcon = when (weather?.weather?.firstOrNull()?.main) {
            "Clear" -> R.drawable.img_sun
            "Clouds" -> R.drawable.img_cloudy
            "Rain" -> R.drawable.img_rain
            "Snow" -> R.drawable.img_snow
            else -> R.drawable.img_sub_rain
        }

        Image(painter = painterResource(weatherIcon), contentDescription =null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .height(175.dp)
                .constrainAs(forecastImage) {
                    start.linkTo(anchor = parent.start, margin = 4.dp)
                    top.linkTo(anchor = parent.top)
                }
            )
        
        Text(text =  formatNumberBasedOnLanguage(weather?.weather?.firstOrNull()?.main?:""),
            style = MaterialTheme.typography.titleLarge,
            color = ColorTextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.constrainAs(title){
                start.linkTo(anchor = parent.start, margin = 24.dp)
                top.linkTo(anchor = forecastImage.bottom)
            }


            )
        val formattedDate = weather?.dt?.let { timestamp ->
            val sdf = SimpleDateFormat("EEE, MMM d, yyyy HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp * 1000L)) // Convert seconds to milliseconds
        } ?: "N/A"

        Text(text =formattedDate,
            style = MaterialTheme.typography.titleLarge,
            color = ColorTextSecondaryVariant,
            modifier = Modifier
                .constrainAs(description) {
                    start.linkTo(anchor = title.start)
                    top.linkTo(anchor = title.bottom)
                }
                .padding(bottom = 24.dp)

        )

        ForeCastValue(modifier = Modifier.constrainAs(forecastValue){
            end.linkTo(anchor = parent.end,margin=24.dp)
            top.linkTo(anchor = forecastImage.top)
            bottom.linkTo(anchor = forecastImage.bottom)
        },
            degree = formatNumberBasedOnLanguage( weather?.main?.temp?.toInt()?.toString() ?:"N/A") ,
            description = stringResource(
                R.string.feels_like,
                weather?.main?.feels_like?.toInt() ?: "N/A"
            )
        )



    }
}

@Composable
private fun cardBackground(modifier: Modifier=Modifier)
{
    Box(modifier= modifier
        .fillMaxWidth()
        .background(
            brush = Brush.linearGradient(
                0f to ColorGradient1,
                0.5f to ColorGradient2,
                1f to ColorGradient3
            ),
            shape = RoundedCornerShape(32.dp)
        )
    ){}
}

@Composable
private fun ForeCastValue(
    modifier: Modifier=Modifier,
    degree:String,
    description:String
){
    Column(modifier=modifier,
        horizontalAlignment = Alignment.Start
        ) {
        Box(contentAlignment = Alignment.TopEnd) {


            Text(
                text = formatNumberBasedOnLanguage(degree),
                style = TextStyle(
                    brush = Brush.linearGradient(
                        0f to Color.White,
                        1f to Color.White.copy(alpha = 0.3f)
                    ),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black
                ),
                modifier=Modifier.padding(end=16.dp)
            )


            Text(
                text = " °",
                Modifier.padding(start = 8.dp),
                style = TextStyle(
                    brush = Brush.linearGradient(
                        0f to Color.White,
                        1f to Color.White.copy(alpha = 0.3f)
                    ),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light
                )
            )

        }
        
        Text(text = description,
            style=MaterialTheme.typography.bodyMedium,
            color= ColorTextSecondaryVariant
            )

        
    }

}

