package com.sd.laborator;

import io.micronaut.function.executor.FunctionInitializer
import io.micronaut.function.FunctionBean
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.function.Function
import javax.xml.parsers.DocumentBuilderFactory


@FunctionBean("consumer")
class ConsumerFunction : FunctionInitializer(), Function<Message, Map<String, String>> {

    override fun apply(msg : Message) : Map<String, String> {
        val response: Map<String, String>
        val content = msg.getMessage()
        response = try {
            parseXML(content)
        } catch (ex: Exception) {
            mapOf(Pair("error", ex.message.toString()))
        }
        return response
    }

    private fun parseXML(xmlText: String): Map<String, String> {
        val result = HashMap<String, String>()
        val builderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = builderFactory.newDocumentBuilder()
        val doc = docBuilder.parse(InputSource(StringReader(xmlText)))

        val entries = doc.getElementsByTagName("entry")
        for (i in 0 until entries.length) {
            if (entries.item(0).nodeType == Node.ELEMENT_NODE) {
                val element = entries.item(i) as Element
                result[getNodeValue(element,"title")] = getNodeAttributeValue(element, "link", "href")
            }
        }

        return result
    }

    private fun getNodeValue(element: Element, tag: String): String{
        val nodeList = element.getElementsByTagName(tag)
        val node = nodeList.item(0)
        if(node != null){
            if(node.hasChildNodes()){
                val child = node.firstChild
                while(child!=null){
                    if(child.nodeType == Node.TEXT_NODE)
                    {
                        return child.nodeValue
                    }
                }
            }
        }
        return ""
    }

    private fun getNodeAttributeValue(element: Element, tag: String, attribute:String): String{
        val nodeList = element.getElementsByTagName(tag)
        return if (nodeList.length > 0) {
            val node = nodeList.item(0)
            node.attributes.getNamedItem(attribute).nodeValue
        }
        else {
            ""
        }
    }
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar 
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) { 
    val function = ConsumerFunction()
    function.run(args, { context -> function.apply(context.get(Message::class.java))})
}