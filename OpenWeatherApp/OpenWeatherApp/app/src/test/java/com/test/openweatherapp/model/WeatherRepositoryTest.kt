package com.santosh.ablertsonassignment.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.test.openweatherapp.data.Weather
import com.test.openweatherapp.data.WeatherModel
import com.test.openweatherapp.data.network.ApiService
import com.test.openweatherapp.data.remote.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherRepositoryTest {

    private lateinit var repository: WeatherRepository

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var api: ApiService

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = WeatherRepository(api, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a weather call, when response body is empty, then emit Error state`() {
        val cityName = "london"

        runTest {
            whenever(api.getWeatherData(cityName)).thenReturn(mockedEmptyResponse())
            assertEquals(ERROR_OCCURRED_MESSAGE, repository.getWeatherInformation(cityName).message)
        }
    }

    @Test
    fun `Given a weather call, when response body is valid, then emit Success state`() {
        val cityName = "london"
        val mockedResponse = mockedNonEmptyResponse()
        val acronymResponse = VALID_RESPONSE
        val expectedResult = WeatherModel(
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

        runTest {
            whenever(api.getWeatherData(cityName)).thenReturn(mockedResponse)
            assertEquals(expectedResult, repository.getWeatherInformation(cityName).data)
        }
    }

    @Test
    fun `Given a weather call, when an unknown exception occurs, then emit Success state`() {
        val city = "test"

        runTest {
            whenever(api.getWeatherData(city)).thenReturn(mockedErrorResponse())
            assertEquals(ERROR_OCCURRED_MESSAGE, repository.getWeatherInformation(city).message)
        }
    }

    private fun mockedNonEmptyResponse(): Response<WeatherModel> =
        Response.success(VALID_RESPONSE)

    private fun mockedErrorResponse(): Response<WeatherModel> =
        Response.error(
            NETWORK_ERROR_CODE,
            SERVICE_DOWN_ERROR_BODY
        )

    private fun mockedEmptyResponse(): Response<WeatherModel> =
        Response.success(
            null
        )

    companion object {
        private const val NETWORK_ERROR_CODE = 404
        const val ERROR_OCCURRED_MESSAGE = "No data found"


        private val VALID_RESPONSE = WeatherModel(
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

        private val SERVICE_DOWN_ERROR_BODY = ResponseBody.create(
            null,
            ERROR_OCCURRED_MESSAGE
        )
    }
}