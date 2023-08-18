package com.test.openweatherapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.test.openweatherapp.FakeRepository
import com.test.openweatherapp.FakeRepository.Companion.weatherResult
import com.test.openweatherapp.data.WeatherPreferences
import com.test.openweatherapp.data.WeatherPreferences.Companion.PREFERENCE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    private lateinit var viewModel: OpenWeatherViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val repository = FakeRepository()

    @Mock
    private lateinit var prefRepository: WeatherPreferences

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val application = Mockito.mock (Application::class.java)
        val preferences: SharedPreferences? =
            Mockito.mock(SharedPreferences::class.java)

        whenever(application.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE))
            .thenReturn(preferences)
        prefRepository = WeatherPreferences(context = application)
        viewModel = OpenWeatherViewModel(
            dataSource = repository,
            prefRepository = prefRepository,
            dispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given weather, when response is unsuccessful, then post Error event`() {
        val city = ""
        val expectedErrorMessage = "No data found"

        runTest {
            viewModel.fetchWeatherReport(city)
            advanceUntilIdle()
            val actualResult =
                viewModel.weatherReport.value as OpenWeatherViewModel.WeatherEvent.Failure
            assertEquals(expectedErrorMessage, actualResult.errorMessage)
        }
    }

    @Test
    fun `Given fetch weather, when response is successful, then post Success event`() {
        val city = "London"

        runTest {
            viewModel.fetchWeatherReport(city)
            advanceUntilIdle()
            val actualResult =
                viewModel.weatherReport.value as OpenWeatherViewModel.WeatherEvent.Success
            assertEquals(weatherResult, actualResult.response)
        }
    }
}