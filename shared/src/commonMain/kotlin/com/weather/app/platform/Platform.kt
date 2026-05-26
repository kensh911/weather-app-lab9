package com.weather.app.platform

import io.ktor.client.engine.HttpClientEngine

expect fun provideHttpEngine(): HttpClientEngine
expect fun currentTimeMs(): Long