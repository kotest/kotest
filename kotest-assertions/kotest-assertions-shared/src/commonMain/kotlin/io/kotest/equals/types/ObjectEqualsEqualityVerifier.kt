package io.kotest.equals.types

import io.kotest.assertions.eq.eq
import io.kotest.equals.EqualityResult
import io.kotest.equals.EqualityVerifier
import io.kotest.equals.EqualityVerifiers

open class ObjectEqualsEqualityVerifier<T>(
   private val strictNumberEquality: Boolean,
) : EqualityVerifier<T> {
   override fun name(): String = "object equality"

   override fun verify(actual: T, expected: T): EqualityResult {
      val throwable = eq(actual, expected, strictNumberEquality) ?: return EqualityResult.equal(actual, expected, this)

      return EqualityResult.notEqual(actual, expected, this).let { result ->
         throwable.message?.let { message ->
            result.withDetails { message }
         } ?: result
      }
   }
}


fun <T> EqualityVerifiers.objectEquality(
   strictNumberEquality: Boolean = false,
): ObjectEqualsEqualityVerifier<T> = ObjectEqualsEqualityVerifier(
   strictNumberEquality = strictNumberEquality,
)
