package com.laborator.sd.interfaces

interface UnionOperation: ChainedServiceInterface {
    fun executeOperation(A: Set<Any>, B: Set<Any>): Set<Any>
}