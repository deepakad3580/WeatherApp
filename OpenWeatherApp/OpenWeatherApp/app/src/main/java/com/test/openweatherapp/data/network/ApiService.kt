package com.test.openweatherapp.data.network

import com.test.openweatherapp.data.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather?appId=68d038d563e5b5c9bda25035aa7fc373")
    suspend fun getWeatherData(
        @Query("q") city: String
    ): Response<WeatherModel>

    @GET("weather?appId=68d038d563e5b5c9bda25035aa7fc373")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<WeatherModel>
}