package com.weather.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.app.model.WeatherData
import com.weather.app.model.WeatherResult
import com.weather.app.model.WeatherUiState
import com.weather.app.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    init {
        WeatherRepository.DEFAULT_CITIES.forEach { loadCity(it) }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun searchCity() {
        val city = _state.value.searchQuery.trim()
        if (city.isBlank()) return
        loadCity(city)
        _state.update { it.copy(searchQuery = "") }
    }

    fun loadCity(city: String) {
        val name = city.trim()
        if (_state.value.loading.contains(name)) return
        _state.update { s ->
            s.copy(loading = s.loading + name, errors = s.errors - name)
        }
        viewModelScope.launch {
            when (val result = repository.getWeather(name)) {
                is WeatherResult.Success -> _state.update { s ->
                    val updated = s.cities.filter { it.city != result.data.city } + result.data
                    s.copy(cities = updated, loading = s.loading - name)
                }
                is WeatherResult.Error -> _state.update { s ->
                    s.copy(
                        loading = s.loading - name,
                        errors  = s.errors + (name to result.message)
                    )
                }
            }
        }
    }

    fun refresh(city: String) {
        _state.update { s -> s.copy(loading = s.loading + city) }
        viewModelScope.launch {
            when (val result = repository.refreshWeather(city)) {
                is WeatherResult.Success -> _state.update { s ->
                    val updated = s.cities.map {
                        if (it.city == result.data.city) result.data else it
                    }
                    s.copy(cities = updated, loading = s.loading - city)
                }
                is WeatherResult.Error -> _state.update { s ->
                    s.copy(
                        loading = s.loading - city,
                        errors  = s.errors + (city to result.message)
                    )
                }
            }
        }
    }

    fun selectCity(data: WeatherData?) {
        _state.update { it.copy(selectedCity = data) }
    }

    fun dismissError(city: String) {
        _state.update { it.copy(errors = it.errors - city) }
    }
}