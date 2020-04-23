package com.laborator.sd.services

import com.laborator.sd.interfaces.CartesianProductOperation
import com.laborator.sd.interfaces.ChainedServiceInterface
import org.springframework.stereotype.Service
import java.lang.ClassCastException

@Service
class CartesianProductService: CartesianProductOperation {
    private var nextInChain: ChainedServiceInterface? = null
    private val argsForNextInChain = mutableListOf<Set<Any>>()

    override fun executeOperation(A: Set<Int>, B: Set<Int>): Set<Pair<Int, Int>> {
        val result: MutableSet<Pair<Int, Int>> = mutableSetOf()
        A.forEach { a -> B.forEach { b -> result.add(Pair(a, b)) } }
        return result.toSet()
    }

    override fun setNextInChain(nextInChain: ChainedServiceInterface) {
        this.nextInChain = nextInChain
    }

    override fun addArgForNextInChain(arg: Set<Any>) {
        argsForNextInChain.add(arg)
    }

    override fun process(args: List<Set<Any>>): Any? {
        if (args.isEmpty() || 2 < args.size) {
            println("[CartesianProductService]Error: The number of arguments must be 1 or 2.")
            return null
        }

        val result: MutableSet<Pair<Int, Int>> = mutableSetOf()
        try{
            @Suppress("UNCHECKED_CAST")
            when (args.size) {
                1 -> args[0].forEach { a -> args[0].forEach { b -> result.add(Pair(a, b) as Pair<Int, Int>) } }
                2 -> args[0].forEach { a -> args[1].forEach { b -> result.add(Pair(a, b) as Pair<Int, Int>) } }
            }
        } catch (e: ClassCastException) {
            println("[CartesianProductService]Error: The cartesian product can be computed only between sets of integers.")
            return null
        }

        if (null == nextInChain) {
            return result
        } else {
            return nextInChain?.process(argsForNextInChain)
        }
    }
}