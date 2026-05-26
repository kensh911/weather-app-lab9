package com.weather.app.cache

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

lateinit var appContext: Context

actual fun createSettings(): Settings =
    SharedPreferencesSettings(appContext.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE))