package com.sd.laborator.services

import com.sd.laborator.interfaces.ServiceInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URL
import kotlin.math.roundToInt

@Service
class WeatherForecastService (private val timeService: TimeService) : ServiceInterface {
    private var nextInChain: ServiceInterface? = null

    override fun setNextInChain(nextInChain: ServiceInterface) {
        this.nextInChain = nextInChain
    }

    override fun process(input: String): String? {
        // ID-ul locaţiei nu trebuie codificat, deoarece este numeric
        val forecastDataURL = URL("https://www.metaweather.com/api/location/$input/")

        // preluare conţinut răspuns HTTP la o cerere GET către URL-ul de mai sus
        val rawResponse: String = forecastDataURL.readText()

        // parsare obiect JSON primit
        val responseRootObject = JSONObject(rawResponse)
        val weatherDataObject = responseRootObject.getJSONArray("consolidated_weather").getJSONObject(0)

        // construire obiect POJO care încapsulează datele meteo
        val weatherForecastData = WeatherForecastData(
            location = responseRootObject.getString("title"),
            date = timeService.getCurrentTime(),
            weatherState = weatherDataObject.getString("weather_state_name"),
            weatherStateIconURL =
                "https://www.metaweather.com/static/img/weather/png/${weatherDataObject.getString("weather_state_abbr")}.png",
            windDirection = weatherDataObject.getString("wind_direction_compass"),
            windSpeed = weatherDataObject.getFloat("wind_speed").roundToInt(),
            minTemp = weatherDataObject.getFloat("min_temp").roundToInt(),
            maxTemp = weatherDataObject.getFloat("max_temp").roundToInt(),
            currentTemp = weatherDataObject.getFloat("the_temp").roundToInt(),
            humidity = weatherDataObject.getFloat("humidity").roundToInt()
        )

        if (null == nextInChain) {
            return weatherForecastData.toString()
        }
        return nextInChain?.process(weatherForecastData.toString())
    }
}