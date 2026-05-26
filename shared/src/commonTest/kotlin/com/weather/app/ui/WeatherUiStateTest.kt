package com.weather.app.ui

import com.weather.app.model.ForecastDay
import com.weather.app.model.WeatherData
import com.weather.app.model.WeatherUiState
import kotlin.test.*

class WeatherUiStateTest {

    private val city = WeatherData(
        "Минск", "BY", 53.9, 27.57,
        15.0, 13.0, 10.0, 60, "Ясно", emptyList()
    )

    @Test
    fun initialState_isEmpty() {
        val state = WeatherUiState()
        assertTrue(state.cities.isEmpty())
        assertTrue(state.loading.isEmpty())
        assertTrue(state.errors.isEmpty())
        assertNull(state.selectedCity)
        assertEquals("", state.searchQuery)
    }

    @Test
    fun addCity_updatesCorrectly() {
        val state = WeatherUiState(cities = listOf(city))
        assertEquals(1, state.cities.size)
        assertEquals("Минск", state.cities.first().city)
    }

    @Test
    fun loadingFlag_setAndCleared() {
        val loading = WeatherUiState(loading = setOf("Минск"))
        assertTrue(loading.loading.contains("Минск"))
        val cleared = loading.copy(loading = loading.loading - "Минск")
        assertFalse(cleared.loading.contains("Минск"))
    }

    @Test
    fun error_addedAndDismissed() {
        val withError = WeatherUiState(errors = mapOf("Минск" to "Не найден"))
        assertTrue(withError.errors.containsKey("Минск"))
        val cleared = withError.copy(errors = withError.errors - "Минск")
        assertFalse(cleared.errors.containsKey("Минск"))
    }

    @Test
    fun selectedCity_setsAndClears() {
        val state = WeatherUiState(selectedCity = city)
        assertNotNull(state.selectedCity)
        assertNull(state.copy(selectedCity = null).selectedCity)
    }

    @Test
    fun forecast_7days_correctCount() {
        val forecast = (1..7).map { ForecastDay("2024-01-0$it", 15.0, 5.0, 0.0, 10.0) }
        assertEquals(7, city.copy(forecast = forecast).forecast.size)
    }

    @Test
    fun searchQuery_updatesCorrectly() {
        val state = WeatherUiState(searchQuery = "Брест")
        assertEquals("Брест", state.searchQuery)
    }
}