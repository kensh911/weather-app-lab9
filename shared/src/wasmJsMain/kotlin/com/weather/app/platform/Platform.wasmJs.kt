package com.weather.app.platform

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

actual fun provideHttpEngine(): HttpClientEngine = Js.create()
actual fun currentTimeMs(): Long = kotlinx.browser.window.performance.now().toLong()