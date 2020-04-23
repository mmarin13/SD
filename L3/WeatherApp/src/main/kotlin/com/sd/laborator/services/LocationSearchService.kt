package com.sd.laborator.services

import com.sd.laborator.interfaces.ServiceInterface
import org.springframework.stereotype.Service
import java.net.URL
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class LocationSearchService : ServiceInterface {
    private var nextInChain: ServiceInterface? = null

    override fun setNextInChain(nextInChain: ServiceInterface) {
        this.nextInChain = nextInChain
    }

    override fun process(input: String): String? {
        // codificare parametru URL (deoarece poate conţine caractere speciale)
        val encodedLocationName = URLEncoder.encode(input, StandardCharsets.UTF_8.toString())

        // construire obiect de tip URL
        val locationSearchURL = URL("https://www.metaweather.com/api/location/search/?query=$encodedLocationName")

        // preluare raspuns HTTP (se face cerere GET şi se preia conţinutul răspunsului sub formă de text)
        val rawResponse: String = locationSearchURL.readText()

        // parsare obiect JSON
        val responseRootObject = JSONObject("{\"data\": ${rawResponse}}")
        val responseContentObject = responseRootObject.getJSONArray("data").takeUnless { it.isEmpty }
            ?.getJSONObject(0)
        val result = responseContentObject?.getInt("woeid") ?: -1

        if (-1 == result) {
            return "Nu s-au putut gasi date meteo folosind cuvintele cheie \"$input\"!"
        }
        return nextInChain?.process(result.toString())
    }
}