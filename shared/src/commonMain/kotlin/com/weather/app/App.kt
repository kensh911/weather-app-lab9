package com.weather.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.weather.app.model.WeatherData
import com.weather.app.ui.screens.DetailScreen
import com.weather.app.ui.screens.MainScreen
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            var selectedCity by remember { mutableStateOf<WeatherData?>(null) }
            if (selectedCity == null) {
                MainScreen(onCityClick = { selectedCity = it })
            } else {
                DetailScreen(
                    weather = selectedCity!!,
                    onBack  = { selectedCity = null }
                )
            }
        }
    }
}