package com.sd.laborator.interfaces

interface ServiceInterface {
    fun setNextInChain(nextInChain: ServiceInterface)
    fun process(input: String): String?
}