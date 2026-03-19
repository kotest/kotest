package io.kotest.assertions.equals

import io.kotest.assertions.eq.EqCompare
import io.kotest.assertions.eq.EqContext
import io.kotest.assertions.eq.EqResult

open class ObjectEqualsEquality<T>(
   private val strictNumberEquality: Boolean,
) : Equality<T> {
   override fun name(): String = "object equality"

   override fun verify(actual: T, expected: T): EqualityResult {
      return when (val result = EqCompare.compare(actual, expected, EqContext(strictNumberEquality))) {
         is EqResult.Failure -> {
            val throwable = result.error()
            EqualityResult.notEqual(actual, expected, this).let { result ->
               throwable.message?.let { message ->
                  result.withDetails { message }
               } ?: result
            }
         }
         EqResult.Success -> return EqualityResult.equal(actual, expected, this)
      }
   }
}

fun <T> Equality.Companion.byObjectEquality(
   strictNumberEquality: Boolean = false,
) = ObjectEqualsEquality<T>(
   strictNumberEquality = strictNumberEquality,
)
