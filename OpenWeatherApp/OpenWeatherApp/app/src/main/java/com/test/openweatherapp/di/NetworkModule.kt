package com.test.openweatherapp.di

import android.content.Context
import com.test.openweatherapp.BuildConfig
import com.test.openweatherapp.data.WeatherPreferences
import com.test.openweatherapp.data.network.ApiService
import com.test.openweatherapp.data.remote.DataSource
import com.test.openweatherapp.data.remote.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    fun provideAcronymApi(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideWeatherRepository(
        api: ApiService,
        dispatcher: CoroutineDispatcher,
    ): DataSource = WeatherRepository(api, dispatcher)

    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun providePrefRepository(
        @ApplicationContext appContext: Context
    ): WeatherPreferences = WeatherPreferences(appContext)
}