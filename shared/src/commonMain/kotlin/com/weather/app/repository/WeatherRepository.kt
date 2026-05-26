package com.weather.app.repository

import com.weather.app.api.WeatherApi
import com.weather.app.cache.WeatherCache
import com.weather.app.model.*

class WeatherRepository(
    private val api: WeatherApi,
    private val cache: WeatherCache
) {
    companion object {
        val DEFAULT_CITIES = listOf("Минск", "Брест")
    }

    suspend fun getWeather(cityName: String): WeatherResult {
        val name = cityName.trim()
        if (name.isBlank()) return WeatherResult.Error(cityName, "Пустое название города")

        val fresh = cache.getFresh(name)
        if (fresh != null) return WeatherResult.Success(fresh)

        return try {
            fetchFromNetwork(name)
        } catch (e: Exception) {
            println("Network error for '$name': ${e.message}")
            val stale = cache.getStale(name)
            if (stale != null) WeatherResult.Success(stale)
            else WeatherResult.Error(name, "Нет соединения: ${e.message}")
        }
    }

    suspend fun refreshWeather(cityName: String): WeatherResult {
        return try {
            fetchFromNetwork(cityName.trim())
        } catch (e: Exception) {
            println("Refresh error: ${e.message}")
            val stale = cache.getStale(cityName.trim())
            if (stale != null) WeatherResult.Success(stale)
            else WeatherResult.Error(cityName, "Ошибка обновления: ${e.message}")
        }
    }

    fun getCached(cityName: String): WeatherData? = cache.getStale(cityName.trim())

    private suspend fun fetchFromNetwork(name: String): WeatherResult {
        val geo = api.getCoordinates(name)
        val results = geo.results
        if (results.isNullOrEmpty()) {
            return WeatherResult.Error(name, "Город '$name' не найден")
        }
        val geoData = results.first()
        val forecast = api.getForecast(geoData.latitude, geoData.longitude)

        val weather = WeatherData(
            city        = geoData.name,
            country     = geoData.country,
            lat         = geoData.latitude,
            lon         = geoData.longitude,
            temperature = forecast.current.temperature,
            feelsLike   = forecast.current.feelsLike,
            windSpeed   = forecast.current.windSpeed,
            humidity    = forecast.current.humidity,
            description = weatherCodeToDescription(forecast.current.weatherCode),
            forecast    = forecast.daily.time.mapIndexed { i, date ->
                ForecastDay(
                    date          = date,
                    maxTemp       = forecast.daily.maxTemp.getOrElse(i) { 0.0 },
                    minTemp       = forecast.daily.minTemp.getOrElse(i) { 0.0 },
                    precipitation = forecast.daily.precipitation.getOrElse(i) { 0.0 },
                    windSpeed     = forecast.daily.windMax.getOrElse(i) { 0.0 }
                )
            }
        )
        cache.save(weather)
        return WeatherResult.Success(weather)
    }
}