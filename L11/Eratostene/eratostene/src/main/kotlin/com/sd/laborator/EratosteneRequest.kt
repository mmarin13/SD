package com.sd.laborator

import io.micronaut.core.annotation.*


@Introspected
class EratosteneRequest {
	private lateinit var numbers: List<Int>

	fun getMaxNumber(): Int {
		var max = numbers.maxBy { it }
		if (null == max)
			max = -1
		return max
	}

	fun getNumbers(): List<Int> {
		return numbers
	}

	fun setNumbers(numbers: List<Int>) {
		this.numbers = numbers
	}
}


