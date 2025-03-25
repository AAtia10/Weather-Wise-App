package com.example.weatherwise.view.settings

import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsScreen(modifier: Modifier = Modifier)
{
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "settings Page")

    }
}