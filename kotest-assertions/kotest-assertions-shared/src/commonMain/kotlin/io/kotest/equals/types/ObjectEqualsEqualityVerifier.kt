package io.kotest.equals.types

import io.kotest.equals.EqualityResult
import io.kotest.equals.EqualityVerifier
import io.kotest.equals.EqualityVerifiers

open class ObjectEqualsEqualityVerifier<T> : EqualityVerifier<T> {
   override fun name(): String = "object equals functions"

   override fun areEqual(actual: T, expected: T): EqualityResult {
      val equal = { EqualityResult.equal(actual = actual, expected = expected, verifier = this) }
      val notEqual = { EqualityResult.notEqual(actual = actual, expected = expected, verifier = this) }

      return when {
         actual === expected -> equal()
         actual == expected -> equal()
         else -> when {
            else -> throw RuntimeException("")
         }
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
}


fun <T> EqualityVerifiers.objectEquality(): ObjectEqualsEqualityVerifier<T> = ObjectEqualsEqualityVerifier()
