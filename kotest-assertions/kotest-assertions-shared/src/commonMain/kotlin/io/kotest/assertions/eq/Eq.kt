package io.kotest.assertions.eq

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.show.show

/**
 * A [Eq] typeclass compares two values for equality, returning an [AssertionError] if they are
 * not equal, or null if they are equal.
 *
 * This equality typeclass is at the heart of the shouldBe matcher.
 */
interface Eq<T> {
   fun equals(actual: T, expected: T, strictNumberEq: Boolean = false): Throwable?
}

/**
 * Locates the applicable [Eq] for the inputs, and invokes it, returning the error if any.
 */
fun <T : Any?> eq(actual: T, expected: T, strictNumberEq: Boolean = false): Throwable? {
   // if we have null and non-null, usually that's a failure, but people can override equals to allow it
   return when {
      actual === expected -> null
      actual == null && expected == null -> null
      actual == null && expected != null && actual != expected -> actualIsNull(expected)
      actual != null && expected == null && actual != expected -> expectedIsNull(actual)
      actual != null && expected != null -> when {
         actual is Map<*, *> && expected is Map<*, *> -> MapEq.equals(actual, expected, strictNumberEq)
         actual is Regex && expected is Regex -> RegexEq.equals(actual, expected)
         actual is String && expected is String -> StringEq.equals(actual, expected)
         actual is Number && expected is Number -> NumberEq.equals(actual, expected, strictNumberEq)
         IterableEq.isValidIterable(actual) && IterableEq.isValidIterable(expected) -> {
            IterableEq.equals(IterableEq.asIterable(actual), IterableEq.asIterable(expected), strictNumberEq)
         }
         shouldShowDataClassDiff(actual, expected) -> DataClassEq.equals(actual as Any, expected as Any, strictNumberEq)
         actual is Throwable && expected is Throwable -> ThrowableEq.equals(actual, expected)
         else -> DefaultEq.equals(actual as Any, expected as Any,strictNumberEq)
      }
      else -> null
   }
}

private fun <T> shouldShowDataClassDiff(actual: T, expected: T) =
   AssertionsConfig.showDataClassDiff && isDataClassInstance(actual) && isDataClassInstance(expected)

fun actualIsNull(expected: Any): AssertionError {
   return AssertionError("Expected ${expected.show().value} but actual was null")
}

fun expectedIsNull(actual: Any): AssertionError {
   return AssertionError("Expected null but actual was ${actual.show().value}")
}
