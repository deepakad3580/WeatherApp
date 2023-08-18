package com.test.openweatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.openweatherapp.data.WeatherPreferences
import com.test.openweatherapp.data.Resource
import com.test.openweatherapp.data.WeatherModel
import com.test.openweatherapp.data.remote.DataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenWeatherViewModel @Inject constructor(
    private val dataSource: DataSource,
    private val prefRepository: WeatherPreferences,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _weatherReport = MutableLiveData<WeatherEvent>()


    val weatherReport: LiveData<WeatherEvent>
        get() = _weatherReport

    private val weatherData = MutableLiveData<WeatherModel?>()

    fun fetchWeatherReport(city: String) {
        prefRepository.saveCity(city)
        viewModelScope.launch(dispatcher) {
            _weatherReport.postValue(WeatherEvent.Loading)

            handleResponse(dataSource.getWeatherInformation(city))
        }
    }

    fun fetchWeatherReport(lat: Double, lon: Double) {

        viewModelScope.launch(dispatcher) {
            _weatherReport.postValue(WeatherEvent.Loading)

            handleResponse(dataSource.getWeatherInformation(lat, lon))
        }
    }

    private fun handleResponse(response: Resource<WeatherModel>) {
        when (response) {
            is Resource.Error -> _weatherReport.postValue(WeatherEvent.Failure(response.message!!))
            is Resource.Success -> {
                _weatherReport.postValue(WeatherEvent.Success(response.data!!))
                weatherData.postValue(response.data)
            }
        }
    }

    sealed class WeatherEvent {
        class Success(val response: WeatherModel) : WeatherEvent()
        class Failure(val errorMessage: String) : WeatherEvent()
        object Loading : WeatherEvent()
    }
}