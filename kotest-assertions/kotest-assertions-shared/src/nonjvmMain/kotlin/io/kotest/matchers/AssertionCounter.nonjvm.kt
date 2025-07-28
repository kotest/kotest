package io.kotest.matchers

actual val assertionCounter: AssertionCounter = NoopAssertionsCounter

/**
 * A no-operation implementation of [AssertionCounter] that does nothing.
 *
 * This is used when assertions are not counted, such as in non-JVM environments.
 */
object NoopAssertionsCounter : AssertionCounter {
   override fun get(): Int = 1
   override fun reset() {}
   override fun inc() {}
}
