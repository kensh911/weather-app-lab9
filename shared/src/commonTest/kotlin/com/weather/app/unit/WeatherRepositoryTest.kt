package com.weather.app.unit

import com.russhwolf.settings.MapSettings
import com.weather.app.api.WeatherApi
import com.weather.app.cache.WeatherCache
import com.weather.app.model.WeatherResult
import com.weather.app.repository.WeatherRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.*

class WeatherRepositoryTest {

    private val geoJson = """
        {"results":[{"name":"Минск","latitude":53.9,"longitude":27.57,
        "country":"BY","admin1":"Minsk"}]}
    """.trimIndent()

    private val forecastJson = """
        {"current":{"temperature_2m":15.0,"apparent_temperature":13.0,
        "relative_humidity_2m":60,"wind_speed_10m":10.0,"weather_code":0},
        "daily":{"time":["2024-01-01","2024-01-02","2024-01-03",
        "2024-01-04","2024-01-05","2024-01-06","2024-01-07"],
        "temperature_2m_max":[16.0,17.0,18.0,15.0,14.0,13.0,12.0],
        "temperature_2m_min":[10.0,11.0,12.0,9.0,8.0,7.0,6.0],
        "precipitation_sum":[0.0,1.0,0.0,2.0,0.0,0.0,0.5],
        "wind_speed_10m_max":[12.0,14.0,10.0,16.0,8.0,9.0,11.0]}}
    """.trimIndent()

    private fun buildRepo(
        geoBody: String = geoJson,
        forecastBody: String = forecastJson
    ): WeatherRepository {
        var call = 0
        val engine = MockEngine {
            call++
            respond(
                content = if (call == 1) geoBody else forecastBody,
                status  = HttpStatusCode.OK,
                headers = headersOf("Content-Type", ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        return WeatherRepository(WeatherApi(client), WeatherCache(MapSettings()))
    }

    @Test
    fun getWeather_emptyCity_returnsError() = runTest {
        val result = buildRepo().getWeather("")
        assertTrue(result is WeatherResult.Error)
    }

    @Test
    fun getWeather_blankCity_returnsError() = runTest {
        val result = buildRepo().getWeather("   ")
        assertTrue(result is WeatherResult.Error)
    }

    @Test
    fun getWeather_validCity_returnsSuccess() = runTest {
        val result = buildRepo().getWeather("Минск")
        assertTrue(result is WeatherResult.Success)
        assertEquals("Минск", (result as WeatherResult.Success).data.city)
    }

    @Test
    fun getWeather_cityNotFound_returnsError() = runTest {
        val result = buildRepo(geoBody = """{"results":null}""").getWeather("НесуществующийГород")
        assertTrue(result is WeatherResult.Error)
    }

    @Test
    fun getWeather_forecastHas7Days() = runTest {
        val result = buildRepo().getWeather("Минск") as WeatherResult.Success
        assertEquals(7, result.data.forecast.size)
    }

    @Test
    fun getWeather_secondCall_usesCacheSuccess() = runTest {
        val repo = buildRepo()
        repo.getWeather("Минск")
        val second = repo.getWeather("Минск")
        assertTrue(second is WeatherResult.Success)
    }

    @Test
    fun defaultCities_containsMinskAndBrest() {
        val cities = WeatherRepository.DEFAULT_CITIES
        assertTrue(cities.contains("Минск"))
        assertTrue(cities.contains("Брест"))
    }
}