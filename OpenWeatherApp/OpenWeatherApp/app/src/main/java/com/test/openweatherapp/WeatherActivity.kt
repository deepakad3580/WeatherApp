package com.test.openweatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.test.openweatherapp.WeatherUtility.isLocationPermissionGranted
import com.test.openweatherapp.data.WeatherPreferences
import com.test.openweatherapp.databinding.ActivityWeatherBinding
import com.test.openweatherapp.viewmodel.OpenWeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WeatherActivity : ComponentActivity() {
    private val viewModel: OpenWeatherViewModel by viewModels()
    private lateinit var binding: ActivityWeatherBinding
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var prefRepository: WeatherPreferences

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // if User has just allowed for permission
            accessLocation()
        } else {
            // Fetching results for last searched city
            prefRepository.getCityName()?.let {
                if (it.isNotEmpty()) {
                    viewModel.fetchWeatherReport(city = it)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        binding.viewModel = viewModel

        if (isLocationPermissionGranted(this)) {
            // Location permission was already available
            accessLocation()
        } else {
            // Ask for Location Permission
            startLocationPermissionRequest()
        }

        lifecycleScope.launch {
            handleObserver()
        }

        binding.buttonSearch.setOnClickListener {
            val cityName = binding.inputText.text?.toString() ?: return@setOnClickListener
            if (cityName.isNotEmpty()) {
                viewModel.fetchWeatherReport(city = cityName)
            } else {
                binding.inputText.error = getString(R.string.enter_city)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun accessLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {
            locationManager.requestLocationUpdates(
                if (hasGps) LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                locationListener
            )
        }
    }

    private fun handleObserver() {
        viewModel.weatherReport.observe(this@WeatherActivity) {
            when (it) {
                is OpenWeatherViewModel.WeatherEvent.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.errorLayout.visibility = View.GONE
                    binding.weatherLayout.visibility = View.GONE
                }

                is OpenWeatherViewModel.WeatherEvent.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                    binding.weatherLayout.visibility = View.VISIBLE
                    it.response.apply {
                        binding.apply {
                            txtLocation.text = name
                            txtHumidity.text = String.format(getString(R.string.wind, wind.speed))
                            txtTemperature.text =
                                String.format(getString(R.string.temp_degree, main.temp))
                            txtDecription.text = weather.firstOrNull()?.description
                            Glide.with(icon)
                                .load(weather.firstOrNull()?.icon?.let { it1 -> WeatherUtility.getImageUrl(it1) })
                                .into(icon)
                        }
                    }
                }

                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.weatherLayout.visibility = View.GONE
                    binding.errorLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            viewModel.fetchWeatherReport(location.latitude, location.longitude)
            clearCallbacks()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun clearCallbacks() {
        locationManager.removeUpdates(locationListener)
    }

    private fun startLocationPermissionRequest() {
        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    companion object {
        const val IMAGE_END_URL = "@4x.png"
    }
}