package io.kotlintest.assertions

class StringComparisonFailure(override val message: String, val expected: String, val actual: String) : RuntimeException(message)