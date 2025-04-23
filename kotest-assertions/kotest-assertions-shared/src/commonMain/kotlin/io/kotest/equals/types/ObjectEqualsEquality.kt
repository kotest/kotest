package io.kotest.equals.types

import io.kotest.assertions.eq.EqCompare
import io.kotest.equals.Equality
import io.kotest.equals.EqualityResult

open class ObjectEqualsEquality<T>(
   private val strictNumberEquality: Boolean,
) : Equality<T> {
   override fun name(): String = "object equality"

   override fun verify(actual: T, expected: T): EqualityResult {
      val throwable = EqCompare.compare(actual, expected, strictNumberEquality) ?: return EqualityResult.equal(actual, expected, this)

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
