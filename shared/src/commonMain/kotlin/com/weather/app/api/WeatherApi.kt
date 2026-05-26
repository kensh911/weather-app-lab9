package com.weather.app.api

import com.weather.app.model.ForecastResponse
import com.weather.app.model.GeoResponse
import com.weather.app.platform.provideHttpEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class WeatherApi(private val client: HttpClient) {

    companion object {
        const val GEO_URL      = "https://geocoding-api.open-meteo.com/v1/search"
        const val FORECAST_URL = "https://api.open-meteo.com/v1/forecast"
    }

    suspend fun getCoordinates(city: String): GeoResponse {
        return client.get(GEO_URL) {
            parameter("name", city)
            parameter("count", 1)
            parameter("language", "ru")
            parameter("format", "json")
        }.body()
    }

    suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        return client.get(FORECAST_URL) {
            parameter("latitude", lat)
            parameter("longitude", lon)
            parameter("current",
                "temperature_2m,apparent_temperature," +
                        "relative_humidity_2m,wind_speed_10m,weather_code")
            parameter("daily",
                "temperature_2m_max,temperature_2m_min," +
                        "precipitation_sum,wind_speed_10m_max")
            parameter("timezone", "auto")
            parameter("forecast_days", 7)
        }.body()
    }
}

fun buildHttpClient(): HttpClient = HttpClient(provideHttpEngine()) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true; isLenient = true })
    }
    install(Logging) { level = LogLevel.INFO }
}