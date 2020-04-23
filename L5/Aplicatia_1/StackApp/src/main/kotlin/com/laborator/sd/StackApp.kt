package com.laborator.sd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StackApp

fun main(args: Array<String>) {
    runApplication<StackApp>(*args)
}