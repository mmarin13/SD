package com.laborator.sd.interfaces

interface ChainedServiceInterface {
    fun setNextInChain(nextInChain: ChainedServiceInterface)
    fun addArgForNextInChain(arg: Set<Any>)
    fun process(args: List<Set<Any>>): Any?
}