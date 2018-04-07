package io.kotlintest.properties

class PropertyAssertionError(val e: AssertionError, val attempt: Int, val values: List<Any?>) :
    AssertionError("Property failed for\n${values.joinToString("\n")}\nafter $attempt attempts")