package io.kotest.equals.types

import io.kotest.assertions.eq.MapEq
import io.kotest.equals.EqualityResult
import io.kotest.equals.EqualityVerifier
import io.kotest.equals.EqualityVerifiers

open class ObjectEqualsEqualityVerifier<T>(
   private val strictNumberEquality: Boolean = false
) : EqualityVerifier<T> {
   override fun name(): String = "object equals functions"

   override fun areEqual(actual: T, expected: T): EqualityResult {
      val equal = { EqualityResult.equal(actual = actual, expected = expected, verifier = this) }
      val notEqual = { EqualityResult.notEqual(actual = actual, expected = expected, verifier = this) }

      return when {
         actual === expected -> equal()
         actual is Map<*, *> && expected is Map<*, *> ->
            MapEqualityVerifier(strictNumberEquality).areEqual(actual, expected)
         actual is Regex && expected is Regex ->
            RegexEqualityVerifier().areEqual(actual, expected)
         actual == expected -> equal()
         else -> throw RuntimeException("")
      }
//         actual != null && expected != null -> when {
//            actual is Map<*, *> && expected is Map<*, *> -> MapEq.equals(actual, expected, strictNumberEq)
//            actual is Regex && expected is Regex -> RegexEq.equals(actual, expected)
//            actual is String && expected is String -> StringEq.equals(actual, expected)
//            actual is Number && expected is Number -> NumberEq.equals(actual, expected, strictNumberEq)
//            IterableEq.isValidIterable(actual) && IterableEq.isValidIterable(expected) -> {
//               IterableEq.equals(IterableEq.asIterable(actual), IterableEq.asIterable(expected), strictNumberEq)
//            }
//            actual is Sequence<*> && expected is Sequence<*> -> SequenceEq.equals(actual, expected, strictNumberEq)
//            shouldShowDataClassDiff(actual, expected) -> DataClassEq.equals(
//               actual as Any, expected as Any, strictNumberEq
//            )
//            actual is Throwable && expected is Throwable -> ThrowableEq.equals(actual, expected)
//            else -> DefaultEq.equals(actual as Any, expected as Any, strictNumberEq)
//         }
//         else -> null
//      }
//
//      override fun toString(): String = name()
   }

   fun withStrictNumberEquality(): ObjectEqualsEqualityVerifier<T> {
      return ObjectEqualsEqualityVerifier(true)
   }

   fun withoutStrictNumberEquality(): ObjectEqualsEqualityVerifier<T> {
      return ObjectEqualsEqualityVerifier(false)
   }
}


fun <T> EqualityVerifiers.objectEquality(): ObjectEqualsEqualityVerifier<T> = ObjectEqualsEqualityVerifier()
