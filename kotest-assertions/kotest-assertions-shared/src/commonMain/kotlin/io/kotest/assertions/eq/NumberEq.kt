package io.kotest.assertions.eq

import io.kotest.assertions.ActualWithType
import io.kotest.assertions.ExpectedWithType
import io.kotest.assertions.failureWithTypeInformation
import io.kotest.assertions.print.printWithType

object NumberEq : Eq<Number> {

   override fun equals(actual: Number, expected: Number, strictNumberEq: Boolean): Throwable? {
      return if (compare(actual, expected, strictNumberEq)) null
      else failureWithTypeInformation(
         ExpectedWithType(expected.printWithType()),
         ActualWithType(actual.printWithType()),
      )
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
