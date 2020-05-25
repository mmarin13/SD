package com.sd.laborator

import io.micronaut.core.annotation.*


@Introspected
class Message {
	private lateinit var message: String

	fun getMessage(): String {
		return message
	}

	fun setMessage(message: String) {
		this.message = message
	}
}


