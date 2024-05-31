package io.kotest.equals.types

import io.kotest.assertions.eq.eq
import io.kotest.equals.EqualityResult
import io.kotest.equals.Equality

open class ObjectEqualsEquality<T>(
   private val strictNumberEquality: Boolean,
) : Equality<T> {
   override fun name(): String = "object equality"

   override fun verify(actual: T, expected: T): EqualityResult {
      val throwable = eq(actual, expected, strictNumberEquality).failureOrNull() ?: return EqualityResult.equal(actual, expected, this)

      return EqualityResult.notEqual(actual, expected, this).let { result ->
         throwable.message?.let { message ->
            result.withDetails { message }
         } ?: result
      }
   }
}

fun <T> Equality.Companion.byObjectEquality(
   strictNumberEquality: Boolean = false,
) = ObjectEqualsEquality<T>(
   strictNumberEquality = strictNumberEquality,
)
