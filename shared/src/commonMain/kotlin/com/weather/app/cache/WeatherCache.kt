package com.weather.app.cache

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import com.weather.app.model.ForecastDay
import com.weather.app.model.WeatherData
import com.weather.app.platform.currentTimeMs
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
private data class CachedWeather(
    val city: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val temperature: Double,
    val feelsLike: Double,
    val windSpeed: Double,
    val humidity: Int,
    val description: String,
    val forecast: List<CachedForecastDay>,
    val savedAt: Long
)

@Serializable
private data class CachedForecastDay(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val precipitation: Double,
    val windSpeed: Double
)

class WeatherCache(private val settings: Settings) {

    companion object {
        private const val TTL_MS = 10 * 60 * 1000L
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun save(data: WeatherData) {
        val cached = CachedWeather(
            city        = data.city,
            country     = data.country,
            lat         = data.lat,
            lon         = data.lon,
            temperature = data.temperature,
            feelsLike   = data.feelsLike,
            windSpeed   = data.windSpeed,
            humidity    = data.humidity,
            description = data.description,
            forecast    = data.forecast.map {
                CachedForecastDay(it.date, it.maxTemp, it.minTemp, it.precipitation, it.windSpeed)
            },
            savedAt = currentTimeMs()
        )
        try {
            settings[keyFor(data.city)] = json.encodeToString(cached)
        } catch (e: Exception) {
            println("Cache save error: ${e.message}")
        }
    }

    fun getFresh(city: String): WeatherData? {
        val raw = getRaw(city) ?: return null
        if (currentTimeMs() - raw.savedAt > TTL_MS) return null
        return raw.toDomain()
    }

    fun getStale(city: String): WeatherData? = getRaw(city)?.toDomain()

    private fun getRaw(city: String): CachedWeather? {
        val raw: String = settings[keyFor(city)] ?: return null
        return try {
            json.decodeFromString(raw)
        } catch (e: Exception) {
            println("Cache read error: ${e.message}")
            null
        }
    }

    private fun keyFor(city: String) = "weather_${city.lowercase().trim()}"

    private fun CachedWeather.toDomain() = WeatherData(
        city        = city,
        country     = country,
        lat         = lat,
        lon         = lon,
        temperature = temperature,
        feelsLike   = feelsLike,
        windSpeed   = windSpeed,
        humidity    = humidity,
        description = description,
        forecast    = forecast.map {
            ForecastDay(it.date, it.maxTemp, it.minTemp, it.precipitation, it.windSpeed)
        }
    )
}