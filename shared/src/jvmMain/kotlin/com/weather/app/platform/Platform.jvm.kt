package com.weather.app.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

actual fun provideHttpEngine(): HttpClientEngine = CIO.create()
actual fun currentTimeMs(): Long = System.currentTimeMillis()