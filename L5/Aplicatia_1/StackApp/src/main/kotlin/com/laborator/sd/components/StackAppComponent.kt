package com.laborator.sd.components

import com.laborator.sd.interfaces.CartesianProductOperation
import com.laborator.sd.interfaces.ChainedServiceInterface
import com.laborator.sd.interfaces.PrimeNumberGenerator
import com.laborator.sd.interfaces.UnionOperation
import com.laborator.sd.model.Stack
import com.laborator.sd.services.UnionService
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class StackAppComponent {
    private var A: Stack? = null
    private var B: Stack? = null

    @Autowired
    private lateinit var primeGenerator: PrimeNumberGenerator

    @Autowired
    private lateinit var cartesianProductOperation: CartesianProductOperation

    @Autowired
    private lateinit var unionOperation: UnionOperation

    @Autowired
    private lateinit var connectionFactory: RabbitMqConnectionFactoryComponent

    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
    }

    @RabbitListener(queues = ["\${stackapp.rabbitmq.queue}"])
    fun recieveMessage(msg: String) {
        // the result: 114,101,103,101,110,101,114,97,116,101,95,65 - -> needs processing
        val processed_msg = (msg.split(",").map { it.toInt().toChar()}).joinToString(separator="")
        var result: String? = when(processed_msg) {
            "compute" -> computeExpression()
            "regenerate_A" -> regenerateA()
            "regenerate_B" -> regenerateB()
            else -> null
        }
        println("result: ")
        println(result)
        if (result != null) sendMessage(result)
    }

    fun sendMessage(msg: String) {
        println("message: ")
        println(msg)
        this.amqpTemplate.convertAndSend(connectionFactory.getExchange(), connectionFactory.getRoutingKey(), msg)
    }

    private fun generateStack(count: Int): Stack? {
        if (count < 1)
            return null
        var X: MutableSet<Int> = mutableSetOf()
        while (X.count() < count)
            X.add(primeGenerator.generatePrimeNumber())
        return Stack(X)
    }

    private fun computeExpression(): String {
        if (A == null)
            A = generateStack(20)
        if (B == null)
            B = generateStack(20)
        if (A!!.data.count() == B!!.data.count()) {
            // (A x B) U (B x B) = (A U B) x B [cartesian product is distributive over union]

            // build the chain of services
            unionOperation.setNextInChain(cartesianProductOperation)
            unionOperation.addArgForNextInChain(B!!.data)

            // start the processing of chained services
            val result = unionOperation.process(mutableListOf(A!!.data, B!!.data))

            return "compute~" + "{\"A\": \"" + A?.data.toString() + "\", \"B\": \"" + B?.data.toString() +
                    "\", \"result\": \"" + result.toString() + "\"}"
        }
        return "compute~" + "Error: A.count() != B.count()"
    }

    private fun regenerateA(): String {
        A = generateStack(20)
        return "A~" + A?.data.toString()
    }

    private fun regenerateB(): String {
        B = generateStack(20)
        return "B~" + B?.data.toString()
    }
}