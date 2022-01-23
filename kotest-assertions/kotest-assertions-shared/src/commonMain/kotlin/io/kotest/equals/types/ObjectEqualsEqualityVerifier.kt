package io.kotest.equals.types

import io.kotest.equals.EqualityResult
import io.kotest.equals.EqualityVerifier
import io.kotest.equals.EqualityVerifiers

open class ObjectEqualsEqualityVerifier<T>(
   private val strictNumberEquality: Boolean,
   private val ignoreCase: Boolean,
   private val ignoreOrder: Boolean,
) : EqualityVerifier<T> {
   override fun name(): String = "object equality"

   override fun verify(actual: T, expected: T): EqualityResult {
      val equal = { EqualityResult.equal(actual = actual, expected = expected, verifier = this) }
      val notEqual = { EqualityResult.notEqual(actual = actual, expected = expected, verifier = this) }

      return when {
         actual === expected -> equal()
         actual is String && expected is String ->
            stringEqualityVerifier().verify(actual, expected)
         actual is Map<*, *> && expected is Map<*, *> ->
            mapEqualityVerifier().verify(actual, expected)
         actual is Regex && expected is Regex ->
            regexEqualityVerifier().verify(actual, expected)
         actual is Array<*> && expected is Array<*> ->
            iterableEqualityVerifier().verify(actual.toList(), expected.toList())
         actual is Iterable<*> && expected is Iterable<*> ->
            iterableEqualityVerifier().verify(actual, expected)
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

   protected fun mapEqualityVerifier(): EqualityVerifier<Map<*, *>> = MapEqualityVerifier(
      strictNumberEquality = strictNumberEquality,
      ignoreCase = ignoreCase
   )

   protected fun iterableEqualityVerifier(): EqualityVerifier<Iterable<*>> = IterableEqualityVerifier(
      strictNumberEquality = strictNumberEquality,
      ignoreCase = ignoreCase,
      ignoreOrder = ignoreOrder,
   )

   protected fun stringEqualityVerifier(): EqualityVerifier<String> = StringEqualityVerifier(ignoreCase = true)

   protected fun regexEqualityVerifier(): EqualityVerifier<Regex> = RegexEqualityVerifier()

   fun withStrictNumberEquality() = copy(strictNumberEquality = true)
   fun withoutStrictNumberEquality() = copy(strictNumberEquality = false)
   fun ignoringCase() = copy(ignoreCase = true)
   fun caseSensitive() = copy(ignoreCase = false)
   fun ignoringOrder() = copy(ignoreOrder = true)
   fun orderSensitive() = copy(ignoreOrder = false)

   private fun copy(
      strictNumberEquality: Boolean = this.strictNumberEquality,
      ignoreCase: Boolean = this.ignoreCase,
      ignoreOrder: Boolean = this.ignoreOrder,
   ): ObjectEqualsEqualityVerifier<T> {
      return ObjectEqualsEqualityVerifier(
         strictNumberEquality = strictNumberEquality,
         ignoreCase = ignoreCase,
         ignoreOrder = ignoreOrder,
      )
   }
}


fun <T> EqualityVerifiers.objectEquality(
   strictNumberEquality: Boolean = false,
   ignoreCase: Boolean = false,
   ignoreOrder: Boolean = false,
): ObjectEqualsEqualityVerifier<T> = ObjectEqualsEqualityVerifier(
   strictNumberEquality = strictNumberEquality,
   ignoreCase = ignoreCase,
   ignoreOrder = false,
)
