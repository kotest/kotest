package io.kotest.assertions.equals

import io.kotest.assertions.eq.EqCompare
import io.kotest.assertions.eq.EqContext

open class ObjectEqualsEquality<T>(
   private val strictNumberEquality: Boolean,
) : Equality<T> {
   override fun name(): String = "object equality"

   override fun verify(actual: T, expected: T): EqualityResult {
      val result = EqCompare.compare(actual, expected, EqContext(strictNumberEquality))
      if (result.equal) return EqualityResult.equal(actual, expected, this)

      val throwable = result.error() ?: RuntimeException("Equality comparison failed but no error was returned")
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
