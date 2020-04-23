package com.sd.laborator.components

import com.sd.laborator.interfaces.LibraryDAO
import com.sd.laborator.interfaces.LibraryPrinter
import com.sd.laborator.model.Book
import com.sd.laborator.model.Content
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
class LibraryAppComponent {
    @Autowired
    private lateinit var libraryDAO: LibraryDAO

    @Autowired
    private lateinit var libraryPrinter: LibraryPrinter

    @Autowired
    private lateinit var connectionFactory: RabbitMqConnectionFactoryComponent

    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
    }

    fun sendMessage(msg: String) {
        this.amqpTemplate.convertAndSend(connectionFactory.getExchange(), connectionFactory.getRoutingKey(), msg)
    }

    @RabbitListener(queues = ["\${libraryapp.rabbitmq.queue}"])
    fun recieveMessage(msg: String) {
        // the result needs processing
        val processedMsg = (msg.split(",").map { it.toInt().toChar() }).joinToString(separator="")
        try {
            if (processedMsg.split("::")[0] == "add") {
                val messageBody = processedMsg.split("::")[1]
                val args = messageBody.split(";;")
                if (addBook(Book(Content(
                        args[0].split("=")[1],
                        args[3].split("=")[1],
                        args[1].split("=")[1],
                        args[2].split("=")[1])))) {
                    sendMessage("Cartea a fost adaugata cu succes.")
                }
            } else {
                val printFormat = processedMsg.split("&&")[0].split("::")[1]
                val findParameter = processedMsg.split("&&")[1].split("::")[1]
                val result: String? = when(findParameter) {
                    "all" -> customPrint(printFormat)
                    else -> customFind(findParameter, printFormat)
                }
                if (result != null) sendMessage(result)
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    fun customPrint(format: String): String {
        return when(format) {
            "html" -> libraryPrinter.printHTML(libraryDAO.getBooks())
            "json" -> libraryPrinter.printJSON(libraryDAO.getBooks())
            "xml" -> libraryPrinter.printXML(libraryDAO.getBooks())
            "raw" -> libraryPrinter.printRaw(libraryDAO.getBooks())
            else -> "Not supported format"
        }
    }

    fun customFind(searchParameter: String, format: String): String {
        val (field, value) = searchParameter.split("=")
        val result: Set<Book>

        result =  when(field) {
            "author" -> this.libraryDAO.findAllByAuthor(value)
            "title" -> this.libraryDAO.findAllByTitle(value)
            "publisher" -> this.libraryDAO.findAllByPublisher(value)
            else -> emptySet()
        }

        return when(format) {
            "html" -> libraryPrinter.printHTML(result)
            "json" -> libraryPrinter.printJSON(result)
            "xml" -> libraryPrinter.printXML(result)
            "raw" -> libraryPrinter.printRaw(result)
            else -> "Not supported format"
        }
    }

    fun addBook(book: Book): Boolean {
        return try {
            this.libraryDAO.addBook(book)
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

}