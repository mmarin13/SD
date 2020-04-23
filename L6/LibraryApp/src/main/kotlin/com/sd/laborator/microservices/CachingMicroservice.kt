package com.sd.laborator.microservices

import com.sd.laborator.controllers.RabbitMqController
import com.sd.laborator.interfaces.CachingDAO
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller


@Controller
class CachingMicroservice {
    @Autowired
    private lateinit var cachingDAO: CachingDAO

    @Autowired
    private lateinit var rabbitMqController: RabbitMqController

    private lateinit var amqpTemplate: AmqpTemplate

    @Value("\${libraryapp.rabbitmq.routingkey1}")
    private lateinit var routingKey: String

    @Autowired
    fun initTemplate() {
        rabbitMqController.setRoutingKey(routingKey)
        this.amqpTemplate = rabbitMqController.rabbitTemplate()
    }

    fun sendMessage(message: String) {
        this.amqpTemplate.convertAndSend(rabbitMqController.getExchange(), rabbitMqController.getRoutingKey(), message)
    }

    @RabbitListener(queues = ["\${libraryapp.rabbitmq.queue}"])
    fun fetchMessage(message: String) {
        try {
            val requestType = message.substringBefore(':')
            val body = message.substringAfter(':')

            if ("update" == requestType) {
                val query = body.substringBefore(':')
                val result = body.substringAfter(':')
                cachingDAO.addToCache(query, result)
            } else if ("exists" == requestType) {
                val result = cachingDAO.exists(body)
                if (result.isNullOrEmpty()) {
                    sendMessage("")
                } else {
                    sendMessage((result))
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }
}