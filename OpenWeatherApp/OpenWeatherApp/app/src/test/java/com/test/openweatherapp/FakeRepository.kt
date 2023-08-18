package com.test.openweatherapp

import com.test.openweatherapp.data.Resource
import com.test.openweatherapp.data.Weather
import com.test.openweatherapp.data.WeatherModel
import com.test.openweatherapp.data.remote.DataSource

class FakeRepository : DataSource {

    companion object {

        const val ERROR_OCCURRED_MESSAGE = "No data found"

        val weatherResult = WeatherModel(
            name = "Valid",
            weather = listOf(
                Weather(
                    id = 804,
                    main = "Clouds",
                    description = "overcast clouds",
                    icon = "04n"
                )
            )
        )
    }

    override suspend fun getWeatherInformation(city: String): Resource<WeatherModel> {
        return if (city.isEmpty()) {
            Resource.Error(ERROR_OCCURRED_MESSAGE)
        } else {
            Resource.Success(weatherResult)
        }
    }

    override suspend fun getWeatherInformation(lat: Double, lon: Double): Resource<WeatherModel> {
        return if (lat == 0.0 && lon == 0.0) {
            Resource.Error(ERROR_OCCURRED_MESSAGE)
        } else {
            Resource.Success(weatherResult)
        }
    }
}