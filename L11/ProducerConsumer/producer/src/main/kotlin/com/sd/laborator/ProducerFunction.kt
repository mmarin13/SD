package com.sd.laborator;

import io.micronaut.function.executor.FunctionInitializer
import io.micronaut.function.FunctionBean;
import java.net.URL
import java.util.function.Function;


@FunctionBean("producer")
class ProducerFunction : FunctionInitializer(), Function<Message, Message> {

    override fun apply(url : Message) : Message {
        val response = Message()
        response.setString(URL(url.getString()).readText())
        return response
    }

}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar 
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) { 
    val function = ProducerFunction()
    function.run(args, { context -> function.apply(context.get(Message::class.java))})
}