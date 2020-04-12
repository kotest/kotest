package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.show.show

object NumberEq : Eq<Number> {

   override fun equals(actual: Number, expected: Number): Throwable? {
      return if (compare(actual, expected)) null else failure(Expected(expected.show()), Actual(actual.show()))
   }

   private fun compare(a: Number, b: Number): Boolean {
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
            else -> a == b
         }
         is Double -> when (b) {
            is Float -> a == b.toDouble()
            is Int -> a == b.toDouble()
            is Short -> a == b.toDouble()
            is Byte -> a == b.toDouble()
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
