package io.kotest.assertions.eq

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.failure
import io.kotest.assertions.print.print
import kotlin.js.JsName

/**
 * A [Eq] typeclass compares two values for equality, returning an [AssertionError] if they are
 * not equal, or null if they are equal.
 *
 * This equality typeclass is at the heart of the shouldBe matcher.
 */
interface Eq<T> {
   fun equals(actual: T, expected: T, strictNumberEq: Boolean = false): Throwable?

   @JsName("Equals")
   fun equals(actual: T, expected: T): Throwable? {
      return equals(actual, expected, false)
   }
}

object NullEq : Eq<Any?> {

   override fun equals(actual: Any?, expected: Any?, strictNumberEq: Boolean): Throwable? {
      return when {
         actual == null && expected == null -> null
         actual == null && expected != null -> actualIsNull(expected)
         actual != null && expected == null -> expectedIsNull(actual)
         actual != expected -> error("[$NullEq] should not be used when both values are not null")
         else -> null
      }
   }
}

/**
 * Locates the appropriate [Eq] for the inputs, and invokes it, returning the error if any.
 */
fun <T : Any?> eq(actual: T, expected: T, strictNumberEq: Boolean): Throwable? {
   // if we have null and non null, usually that's a failure, but people can override equals to allow it
   return when {
      actual === expected -> null
      actual == null || expected == null -> NullEq.equals(actual, expected)
      else -> when {
         actual is Map<*, *> && expected is Map<*, *> -> MapEq.equals(actual, expected, strictNumberEq)
         actual is Map.Entry<*, *> && expected is Map.Entry<*, *> -> MapEntryEq.equals(actual, expected, strictNumberEq)
         actual is Regex && expected is Regex -> RegexEq.equals(actual, expected)
         actual is String && expected is String -> StringEq.equals(actual, expected)
         actual is Number && expected is Number -> NumberEq.equals(actual, expected, strictNumberEq)
         IterableEq.isValidIterable(actual) && IterableEq.isValidIterable(expected) -> {
            IterableEq.equals(IterableEq.asIterable(actual), IterableEq.asIterable(expected), strictNumberEq)
         }

         actual is Sequence<*> && expected is Sequence<*> -> SequenceEq.equals(actual, expected, strictNumberEq)
         shouldShowDataClassDiff(actual, expected) -> DataClassEq.equals(actual as Any, expected as Any, strictNumberEq)
         actual is Throwable && expected is Throwable -> ThrowableEq.equals(actual, expected)
         else -> DefaultEq.equals(actual as Any, expected as Any, strictNumberEq)
      }
   }
}

/**
 * Locates the appropriate [Eq] for the inputs, and invokes it, returning the error if any.
 */
fun <T : Any?> eq(actual: T, expected: T): Throwable? {
   return eq(actual, expected, false)
}

private fun <T> shouldShowDataClassDiff(actual: T, expected: T) =
   AssertionsConfig.showDataClassDiff && isDataClassInstance(actual) && isDataClassInstance(expected)

fun actualIsNull(expected: Any): AssertionError {
   return failure("Expected ${expected.print().value} but actual was null")
}

fun expectedIsNull(actual: Any): AssertionError {
   return failure("Expected null but actual was ${actual.print().value}")
}
