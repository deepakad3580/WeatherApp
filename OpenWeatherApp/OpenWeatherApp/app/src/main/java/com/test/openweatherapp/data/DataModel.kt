package com.test.openweatherapp.data

data class WeatherModel(
    val name: String = "",
    val weather: List<Weather> = arrayListOf(),
    val main: Temperature = Temperature(),
    val wind: Wind = Wind()
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Temperature(
    val temp: String = "",
    val feels_like: String = "",
    val temp_min: Float = 0f,
    val temp_max: Float = 0f
)

data class Wind(
    val speed: String = "",
    val deg: String = ""
)