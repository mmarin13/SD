package com.sd.laborator

import io.micronaut.core.annotation.*

@Introspected
class Message {
	lateinit var message: String

	fun getString(): String {
		return message
	}

	fun setString(message: String) {
		this.message = message
	}
}


