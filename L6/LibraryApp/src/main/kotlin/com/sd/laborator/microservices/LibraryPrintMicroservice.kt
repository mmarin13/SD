package com.sd.laborator.microservices

import com.sd.laborator.controllers.RabbitMqController
import com.sd.laborator.interfaces.LibraryDAO
import com.sd.laborator.interfaces.LibraryPrinter
import com.sd.laborator.model.Book
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.concurrent.Semaphore


@Controller
class LibraryPrinterMicroservice {
    @Autowired
    private lateinit var libraryDAO: LibraryDAO

    @Autowired
    private lateinit var libraryPrinter: LibraryPrinter

    @Autowired
    private lateinit var rabbitMqController: RabbitMqController

    private lateinit var amqpTemplate: AmqpTemplate

    @Value("\${libraryapp.rabbitmq.routingkey}")
    private lateinit var routingKey: String

    private var lock = Semaphore(1)
    private var messagesList = mutableListOf<String>()

    @Autowired
    fun initTemplate() {
        rabbitMqController.setRoutingKey(routingKey)
        this.amqpTemplate = rabbitMqController.rabbitTemplate()
    }

    fun sendMessage(message: String) {
        this.amqpTemplate.convertAndSend(rabbitMqController.getExchange(), rabbitMqController.getRoutingKey(), message)
    }

    @RabbitListener(queues = ["\${libraryapp.rabbitmq.queue1}"])
    fun fetchMessage(message: String) {
        try {
            messagesList.add(message)
            if (lock.availablePermits() < 1)
                lock.release()
        } catch (e: Exception) {
            println(e)
        }
    }

    @RequestMapping("/print", method = [RequestMethod.GET])
    @ResponseBody
    fun customPrint(@RequestParam(required = true, name = "format", defaultValue = "raw") format: String): String {
        val query = "print;$format"

        // send query and wait the response
        lock.acquire()
        sendMessage("exists:$query")
        lock.acquire()
        lock.release()

        if (messagesList.size > 0) {
            var result = messagesList.removeAt(messagesList.lastIndex)
            if (result.isNotEmpty()) {
                return result
            } else {
                result =  when(format) {
                    "html" -> libraryPrinter.printHTML(libraryDAO.getBooks())
                    "json" -> libraryPrinter.printJSON(libraryDAO.getBooks())
                    "raw" -> libraryPrinter.printRaw(libraryDAO.getBooks())
                    else -> "Format not implemented"
                }
                sendMessage("update:$query:$result")
                return result
            }
        } else {
            return "Something went wrong in microservices communication..."
        }
    }

    @RequestMapping("/find", method = [RequestMethod.GET])
    @ResponseBody
    fun customFind(@RequestParam(required = false, name = "author", defaultValue = "") author: String,
                   @RequestParam(required = false, name = "title", defaultValue = "") title: String,
                   @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String,
                   @RequestParam(required = false, name = "format", defaultValue = "raw") format: String): String {
        val query = "find;$author;$title;$publisher;$format"

        // send query and wait the response
        lock.acquire()
        sendMessage("exists:$query")
        lock.acquire()
        lock.release()

        if (messagesList.size > 0) {
            var result = messagesList.removeAt(messagesList.lastIndex)
            if (result.isNotEmpty()) {
                return result
            } else {
                val bookList: Set<Book>

                if (author != "")
                    bookList = this.libraryDAO.findAllByAuthor(author)
                else if (title != "")
                    bookList = this.libraryDAO.findAllByTitle(title)
                else if (publisher != "")
                    bookList = this.libraryDAO.findAllByPublisher(publisher)
                else
                    bookList = mutableSetOf()

                result = when(format) {
                    "json" -> libraryPrinter.printJSON(bookList)
                    "raw" -> libraryPrinter.printRaw(bookList)
                    else -> libraryPrinter.printHTML(bookList)
                }
                sendMessage("update:$query:$result")
                return result
            }
        } else {
            return "Something went wrong in microservices communication..."
        }
    }
}