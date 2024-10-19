package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.screens.MainScreens
import com.example.weatherapp.screens.TabLayout
import com.example.weatherapp.ui.theme.WeatherAppTheme
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.android.volley.Request
import com.example.weatherapp.data.WeatherData
import com.example.weatherapp.screens.DialogSearch
import org.json.JSONObject

const val API_KEY = "fcb7bd65dccc415c984142912241810"

class MainActivity : ComponentActivity() {
    @SuppressLint("RememberReturnType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                val daysList = remember {
                    mutableStateOf(listOf<WeatherData>())
                }
                val currentDay = remember {
                    mutableStateOf(
                        WeatherData(
                            "",
                            "",
                            "0",
                            "",
                            "",
                            "0",
                            "0",
                            "",
                        )
                    )
                }
                val dialogState= remember {
                    mutableStateOf(false)
                }
                if (dialogState.value){
                    DialogSearch(dialogState, onSubmit = {
                        getDate(it, this, daysList, currentDay)
                    })
                }
                getDate("Grodno", this, daysList, currentDay)
                Image(
                    painter = painterResource(id = R.drawable.background_main_screen),
                    contentDescription = "im1",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.5f),
                    contentScale = ContentScale.FillBounds
                )
                Column {
                    MainScreens(currentDay, onClickSync = {
                        getDate("Grodno", this@MainActivity, daysList, currentDay)
                    }, onClickSearch = {
                        dialogState.value = true
                    })
                    TabLayout(daysList, currentDay)
                }

            }
        }
    }
}

private fun getDate(
    city: String,
    context: Context,
    daysList: MutableState<List<WeatherData>>,
    currentDay: MutableState<WeatherData>
) {
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
            "&q=$city" +
            "&days=" +
            "3" +
            "&aqi=no&alerts=no"
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val list = getWeatheByDays(response)
            currentDay.value = list[0]
            daysList.value = list
        }, {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}


private fun getWeatheByDays(response: String): MutableList<WeatherData> {
    if (response.isEmpty()) return mutableListOf()
    val list = ArrayList<WeatherData>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject
        list.add(
            WeatherData(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()
            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}

