package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.print

/**
 * An [Eq] for [Number] types. If strictNumberEq is true, then the numbers are compared simply
 * using equals, otherwise, the smaller type is converted to the larger type and compared.
 */
object NumberEq : Eq<Number> {

   override fun equals(actual: Number, expected: Number, strictNumberEq: Boolean): Throwable? {
      return if (compare(actual, expected, strictNumberEq)) null
      else AssertionErrorBuilder.create()
         .withValues(Expected(expected.print()), Actual(actual.print()))
         .build()
   }

   private fun compare(a: Number, b: Number, strictNumberEq: Boolean): Boolean {
      if (strictNumberEq) {
         return a == b
      }
      return when (a) {
         is Int -> when (b) {
            is Long -> a.toLong() == b
            is Double -> a.toDouble() == b
            is Float -> a.toFloat() == b
            is Short -> a == b.toInt()
            is Byte -> a == b.toInt()
            else -> a == b
         }
         is Float -> when (b) {
            is Double -> a.toDouble() == b
            is Int -> a == b.toFloat()
            is Float -> a == b
            else -> a == b
         }
         is Double -> when (b) {
            is Float -> a == b.toDouble()
            is Int -> a == b.toDouble()
            is Short -> a == b.toDouble()
            is Byte -> a == b.toDouble()
            is Double -> a == b
            else -> a == b
         }
         is Long -> when (b) {
            is Int -> a == b.toLong()
            is Short -> a == b.toLong()
            is Byte -> a == b.toLong()
            else -> a == b
         }
         is Short -> when (b) {
            is Long -> a.toLong() == b
            is Int -> a.toInt() == b
            is Byte -> a == b.toShort()
            else -> a == b
         }
         else -> a == b
      }
   }
}
