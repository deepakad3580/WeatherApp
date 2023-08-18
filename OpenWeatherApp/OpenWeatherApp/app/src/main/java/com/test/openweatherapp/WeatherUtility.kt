package com.test.openweatherapp

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object WeatherUtility {

    fun getImageUrl(imageName: String) =
        BuildConfig.ENDPOINT_IMAGE_URL + imageName + WeatherActivity.IMAGE_END_URL

    fun isLocationPermissionGranted(context: Context): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }
}