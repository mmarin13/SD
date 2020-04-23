package com.laborator.sd.interfaces

interface CartesianProductOperation: ChainedServiceInterface {
    fun executeOperation(A: Set<Int>, B: Set<Int>): Set<Pair<Int, Int>>
}