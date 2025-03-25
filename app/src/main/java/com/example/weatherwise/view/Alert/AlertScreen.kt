package com.example.weatherwise.view.Alert

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AlertScreen(modifier: Modifier = Modifier)
{
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Alerts Page")

    }
}