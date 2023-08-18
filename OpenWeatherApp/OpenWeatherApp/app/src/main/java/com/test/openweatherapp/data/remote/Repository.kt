package com.test.openweatherapp.data.remote

import android.util.Log
import com.test.openweatherapp.data.Resource
import com.test.openweatherapp.data.WeatherModel
import com.test.openweatherapp.data.network.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface DataSource {
    suspend fun getWeatherInformation(city: String): Resource<WeatherModel>
    suspend fun getWeatherInformation(lat: Double, lon: Double): Resource<WeatherModel>
}

class WeatherRepository @Inject constructor(
    private val apiService: ApiService,
    private val coroutineDispatcher: CoroutineDispatcher
) : DataSource {
    override suspend fun getWeatherInformation(city: String): Resource<WeatherModel> {
        return try {
            withContext(coroutineDispatcher) {
                val response = apiService.getWeatherData(city)
                val result =
                    response.body() ?: return@withContext Resource.Error(ERROR_OCCURRED_MESSAGE)

                when {
                    response.isSuccessful -> Resource.Success(result)
                    else -> Resource.Error(response.message())
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_OCCURRED_MESSAGE)
        }
    }

    override suspend fun getWeatherInformation(lat: Double, lon: Double): Resource<WeatherModel> {
        return try {
            withContext(coroutineDispatcher) {
                val response = apiService.getWeatherData(lat = lat, lon = lon)
                Log.v("Response = ", "${response.isSuccessful} and data = {response.body()}")
                val result =
                    response.body() ?: return@withContext Resource.Error(ERROR_OCCURRED_MESSAGE)

                when {
                    response.isSuccessful -> Resource.Success(result)
                    else -> Resource.Error(response.message())
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_OCCURRED_MESSAGE)
        }
    }

    companion object {
        const val ERROR_OCCURRED_MESSAGE = "No data found"
    }
}