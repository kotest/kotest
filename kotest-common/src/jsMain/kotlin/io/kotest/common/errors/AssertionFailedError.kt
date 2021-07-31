package io.kotest.common.errors

/**
 * An assertion error for when a value did not match an expected value.
 * Contains a message and a cause and the expected and actual values.
 */
actual open class AssertionFailedError(message: String, val expected: Any, val actual: Any) : AssertionError(message)
