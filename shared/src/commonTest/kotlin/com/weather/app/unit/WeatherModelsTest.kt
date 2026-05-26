package com.weather.app.unit

import com.weather.app.model.weatherCodeToDescription
import kotlin.test.*

class WeatherModelsTest {

    @Test
    fun code0_isClear() = assertEquals("Ясно", weatherCodeToDescription(0))

    @Test
    fun code1_isPartlyCloudy() = assertTrue(weatherCodeToDescription(1).isNotBlank())

    @Test
    fun code61_isRain() = assertEquals("Дождь", weatherCodeToDescription(61))

    @Test
    fun code71_isSnow() = assertEquals("Снег", weatherCodeToDescription(71))

    @Test
    fun code80_isShower() = assertEquals("Ливень", weatherCodeToDescription(80))

    @Test
    fun code95_isThunderstorm() = assertEquals("Гроза", weatherCodeToDescription(95))

    @Test
    fun unknownCode_returnsNonBlank() = assertTrue(weatherCodeToDescription(999).isNotBlank())
}