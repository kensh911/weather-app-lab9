package com.weather.app.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

actual fun provideHttpEngine(): HttpClientEngine = Android.create()
actual fun currentTimeMs(): Long = System.currentTimeMillis()