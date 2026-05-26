package com.weather.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeoResponse(
    val results: List<GeoResult>? = null
)

@Serializable
data class GeoResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String = "",
    @SerialName("admin1") val region: String = ""
)

@Serializable
data class ForecastResponse(
    val current: CurrentWeather,
    val daily: DailyWeather
)

@Serializable
data class CurrentWeather(
    @SerialName("temperature_2m")       val temperature: Double,
    @SerialName("apparent_temperature") val feelsLike: Double,
    @SerialName("relative_humidity_2m") val humidity: Int,
    @SerialName("wind_speed_10m")       val windSpeed: Double,
    @SerialName("weather_code")         val weatherCode: Int
)

@Serializable
data class DailyWeather(
    val time: List<String>,
    @SerialName("temperature_2m_max")  val maxTemp: List<Double>,
    @SerialName("temperature_2m_min")  val minTemp: List<Double>,
    @SerialName("precipitation_sum")   val precipitation: List<Double>,
    @SerialName("wind_speed_10m_max")  val windMax: List<Double>
)

data class WeatherData(
    val city: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val temperature: Double,
    val feelsLike: Double,
    val windSpeed: Double,
    val humidity: Int,
    val description: String,
    val forecast: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val precipitation: Double,
    val windSpeed: Double
)

data class WeatherUiState(
    val cities: List<WeatherData>   = emptyList(),
    val loading: Set<String>        = emptySet(),
    val errors: Map<String, String> = emptyMap(),
    val selectedCity: WeatherData?  = null,
    val searchQuery: String         = ""
)

sealed class WeatherResult {
    data class Success(val data: WeatherData) : WeatherResult()
    data class Error(val city: String, val message: String) : WeatherResult()
}

fun weatherCodeToDescription(code: Int): String = when (code) {
    0          -> "Ясно"
    1, 2, 3    -> "Переменная облачность"
    45, 48     -> "Туман"
    51, 53, 55 -> "Морось"
    61, 63, 65 -> "Дождь"
    71, 73, 75 -> "Снег"
    80, 81, 82 -> "Ливень"
    95         -> "Гроза"
    else       -> "Облачно"
}