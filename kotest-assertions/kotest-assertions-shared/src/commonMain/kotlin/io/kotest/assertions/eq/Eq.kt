package io.kotest.assertions.eq

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.MultiAssertionError
import io.kotest.assertions.eq.EqResult.Equal
import io.kotest.assertions.eq.EqResult.NotEqual
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
   fun equals(actual: T, expected: T, strictNumberEq: Boolean = false): EqResult

   @JsName("Equals")
   fun equals(actual: T, expected: T): EqResult {
      return equals(actual, expected, false)
   }
}

typealias ErrorProducer = () -> Throwable

sealed interface EqResult {
   data object Equal : EqResult
   data class NotEqual(val errorProducer: ErrorProducer) : EqResult

   companion object {
      operator fun invoke(result: Boolean, errorProducer: ErrorProducer): EqResult =
         if (result) Equal else NotEqual(errorProducer)
   }

   fun failureOrNull(): Throwable? = when (this) {
      is Equal -> null
      is NotEqual -> errorProducer()
   }

   fun mapNotEqual(fn: ErrorProducer) = when (this) {
      is Equal -> Equal
      is NotEqual -> NotEqual(errorProducer)
   }
}

fun and(first: EqResult, vararg rest: EqResult): EqResult {
   val failures = (listOf(first) + rest).filterIsInstance<NotEqual>()
   val failureCount = failures.count()

   return when (failureCount) {
      0 -> Equal
      1 -> failures.single()
      else -> NotEqual { MultiAssertionError(failures.map { it.errorProducer() }, depth = 0) }
   }
}

object NullEq : Eq<Any?> {

   override fun equals(actual: Any?, expected: Any?, strictNumberEq: Boolean): EqResult {
      return when {
         actual == null && expected == null -> Equal
         actual == null && expected != null && actual != expected -> NotEqual { actualIsNull(expected) }
         actual != null && expected == null && actual != expected -> NotEqual { expectedIsNull(actual) }
         actual != expected -> error("[$NullEq] should not be used when both values are not null")
         else -> Equal
      }
   }
}

/**
 * Locates the appropriate [Eq] for the inputs, and invokes it, returning the error if any.
 */
fun <T : Any?> eq(actual: T, expected: T, strictNumberEq: Boolean): EqResult {
   // if we have null and non null, usually that's a failure, but people can override equals to allow it
   return when {
      actual === expected -> Equal
      actual == null || expected == null -> NullEq.equals(actual, expected)
      else -> when {
         actual is Map<*, *> && expected is Map<*, *> -> MapEq.equals(actual, expected, strictNumberEq)
         actual is Map.Entry<*, *> && expected is Map.Entry<*, *> -> MapEntryEq.equals(actual, expected, strictNumberEq)
         actual is Regex && expected is Regex -> RegexEq.equals(actual, expected)
         actual is String && expected is String -> StringEq.equals(actual, expected)
         actual is Number && expected is Number -> NumberEq.equals(actual, expected, strictNumberEq)
//         IterableEq.isValidIterable(actual) && IterableEq.isValidIterable(expected) -> {
//            IterableEq.equals(IterableEq.asIterable(actual), IterableEq.asIterable(expected), strictNumberEq)
//         }

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
fun <T : Any?> eq(actual: T, expected: T): EqResult {
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
