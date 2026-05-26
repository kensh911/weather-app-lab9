package com.weather.app.di

import com.weather.app.api.WeatherApi
import com.weather.app.api.buildHttpClient
import com.weather.app.cache.WeatherCache
import com.weather.app.cache.createSettings
import com.weather.app.repository.WeatherRepository
import com.weather.app.viewmodel.WeatherViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single  { buildHttpClient() }
    single  { WeatherApi(get()) }
    single  { WeatherCache(createSettings()) }
    single  { WeatherRepository(get(), get()) }
    viewModelOf(::WeatherViewModel)
}