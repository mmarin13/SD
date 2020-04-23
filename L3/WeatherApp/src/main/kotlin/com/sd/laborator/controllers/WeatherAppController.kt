package com.sd.laborator.controllers

import com.sd.laborator.interfaces.ServiceInterface
import com.sd.laborator.services.LocationSearchService
import com.sd.laborator.services.TimeService
import com.sd.laborator.services.WeatherForecastService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class WeatherAppController {
    private lateinit var chainedService: ServiceInterface

    @RequestMapping("/getforecast/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String {
        // create first service of the chain of services
        chainedService = LocationSearchService()
        // add services to the chain of services
        chainedService.setNextInChain(WeatherForecastService(TimeService()))
        // process the request: start with first service of chain
        return chainedService.process(location) ?: "Something went wrong!"
    }
}