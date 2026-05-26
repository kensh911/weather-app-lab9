package com.weather.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.app.model.ForecastDay
import com.weather.app.model.WeatherData
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    weather: WeatherData,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(weather.city) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Назад", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier            = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text       = "${weather.temperature.roundToInt()}°C",
                            fontSize   = 64.sp,
                            fontWeight = FontWeight.Light,
                            color      = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text  = weather.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            com.weather.app.ui.components.WeatherDetailItem(
                                "Ощущается", "${weather.feelsLike.roundToInt()}°C"
                            )
                            com.weather.app.ui.components.WeatherDetailItem(
                                "Влажность", "${weather.humidity}%"
                            )
                            com.weather.app.ui.components.WeatherDetailItem(
                                "Ветер", "${roundToOneDecimal(weather.windSpeed)} км/ч"
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text  = "Координаты: ${roundToFour(weather.lat)}, ${roundToFour(weather.lon)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            item {
                Text(
                    text       = "Прогноз на 7 дней",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.padding(horizontal = 4.dp)
                )
            }

            items(weather.forecast) { day -> ForecastCard(day) }
        }
    }
}

@Composable
private fun ForecastCard(day: ForecastDay) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier              = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = day.date,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier   = Modifier.weight(1.4f)
            )
            Text(
                text     = "${day.maxTemp.roundToInt()}° / ${day.minTemp.roundToInt()}°",
                style    = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1.5f)
            )
            Text(
                text     = "${roundToOneDecimal(day.precipitation)} мм",
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
            Text(
                text     = "${day.windSpeed.toInt()} км/ч",
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun roundToOneDecimal(value: Double): String {
    val intPart = value.toInt()
    val decPart = ((value - intPart) * 10).toInt()
    return "$intPart.$decPart"
}

private fun roundToFour(value: Double): String {
    val intPart = value.toInt()
    val decPart = ((value - intPart) * 10000).toInt()
    return "$intPart.$decPart"
}