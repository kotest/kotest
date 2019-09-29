package io.kotest

/**
 * Exception to mark a test as ignored while it is already running
 *
 * The SkipTestException may be thrown inside a test case to skip it (mark it as ignored). Any subclass of this class
 * may be used, in case you want to use your specific exception.
 *
 * ```
 * class FooTest : StringSpec({
 *    "Ignore this test!" {
 *        throw SkipTestException("I want to ignore this test!")
 *    }
 * })
 * ```
 */
open class SkipTestException(val reason: String? = null): RuntimeException(reason)
