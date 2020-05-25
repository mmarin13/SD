package com.sd.laborator

import io.micronaut.core.annotation.*


@Introspected
class EratosteneResponse {
    private var message: String? = null
    private var primes: List<Int>? = null

    fun setMessage(message: String?) {
        this.message = message
    }

    fun setPrimes(primes: List<Int>?) {
        this.primes = primes
    }

    fun getMessage(): String? {
        return message
    }

    fun getPrimes(): List<Int>? {
        return primes
    }
}


