package com.test.openweatherapp.data

import android.content.Context
import android.content.SharedPreferences

class WeatherPreferences(private val context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun saveCity(cityName: String) {
        if (editor != null) {
            editor.putString(CITY_NAME, cityName)
            editor.commit()
        }
    }

    fun getCityName(): String? = pref.getString(CITY_NAME, "")

    companion object {
        const val PREFERENCE_NAME = "pref"
        const val CITY_NAME = "city"
    }
}

