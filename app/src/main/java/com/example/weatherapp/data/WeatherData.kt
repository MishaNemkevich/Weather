package com.example.weatherapp.data

import android.service.notification.Condition
import java.sql.Time

data class WeatherData(
    val city: String,
    val time: String,
    val currentTemp: String,
    val condition: String,
    val icon: String,
    val maxTemp: String,
    val minTemp: String,
    val hours: String
)
