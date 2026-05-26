package com.weather.app.unit

import com.russhwolf.settings.MapSettings
import com.weather.app.cache.WeatherCache
import com.weather.app.model.ForecastDay
import com.weather.app.model.WeatherData
import kotlin.test.*

class WeatherCacheTest {

    private fun makeCache() = WeatherCache(MapSettings())

    private val sample = WeatherData(
        city        = "Минск",
        country     = "BY",
        lat         = 53.9,
        lon         = 27.57,
        temperature = 15.0,
        feelsLike   = 13.0,
        windSpeed   = 10.0,
        humidity    = 60,
        description = "Ясно",
        forecast    = listOf(ForecastDay("2024-01-01", 16.0, 10.0, 0.0, 12.0))
    )

    @Test
    fun save_thenGetStale_returnsData() {
        val cache = makeCache()
        cache.save(sample)
        assertNotNull(cache.getStale("Минск"))
    }

    @Test
    fun getStale_unknownCity_returnsNull() {
        assertNull(makeCache().getStale("НетТакого"))
    }

    @Test
    fun save_preservesAllFields() {
        val cache = makeCache()
        cache.save(sample)
        val result = cache.getStale("Минск")!!
        assertEquals("Минск", result.city)
        assertEquals("BY", result.country)
        assertEquals(60, result.humidity)
        assertEquals("Ясно", result.description)
    }

    @Test
    fun save_preservesForecast() {
        val cache = makeCache()
        cache.save(sample)
        val forecast = cache.getStale("Минск")!!.forecast
        assertEquals(1, forecast.size)
        assertEquals("2024-01-01", forecast.first().date)
    }

    @Test
    fun save_multipleCities_independent() {
        val cache = makeCache()
        cache.save(sample)
        cache.save(sample.copy(city = "Брест"))
        assertNotNull(cache.getStale("Минск"))
        assertNotNull(cache.getStale("Брест"))
    }

    @Test
    fun save_overwritesSameCity() {
        val cache = makeCache()
        cache.save(sample)
        cache.save(sample.copy(temperature = 20.0))
        assertEquals(20.0, cache.getStale("Минск")!!.temperature)
    }
}