package com.laborator.sd.services

import com.laborator.sd.interfaces.ChainedServiceInterface
import com.laborator.sd.interfaces.UnionOperation
import org.springframework.stereotype.Service

@Service
class UnionService: UnionOperation {
    private var nextInChain: ChainedServiceInterface? = null
    private val argsForNextInChain = mutableListOf<Set<Any>>()

    override fun executeOperation(A: Set<Any>, B: Set<Any>): Set<Any> {
        return A union B
    }

    override fun setNextInChain(nextInChain: ChainedServiceInterface) {
        this.nextInChain = nextInChain
    }

    override fun addArgForNextInChain(arg: Set<Any>) {
        argsForNextInChain.add(arg)
    }

    override fun process(args: List<Set<Any>>): Any? {
        if (args.isEmpty()) {
            println("[UnionService]Error: The list of arguments can not be empty.")
            return null
        }

        var result: Set<Any> = args[0]
        for (i in 1 until args.size) {
            result = result union args[i]
        }

        if (null == nextInChain) {
            return result
        } else {
            return nextInChain?.process(argsForNextInChain)
        }
    }
}