package io.kotest.equals.types

import io.kotest.equals.EqualityResult
import io.kotest.equals.EqualityVerifier

class NumberEqualityVerifier(
   private val strictNumberEquality: Boolean
) : EqualityVerifier<Number> {
   override fun name(): String = "${if (strictNumberEquality) "strict " else ""} number equality"

   override fun areEqual(actual: Number, expected: Number): EqualityResult {
      if (compare(actual, expected)) {
         return EqualityResult.equal(actual, expected, this)
      }

      return EqualityResult.notEqual(actual, expected, this)
   }

   private fun compare(a: Number, b: Number): Boolean {
      if (strictNumberEquality) {
         return a == b
      }

      val aFloating = isFloating(a)
      val bFloating = isFloating(b)
      return when {
         aFloating && bFloating -> when (a) {
            is Double -> a == b.toDouble()
            is Float -> a == b.toFloat()
            else -> a == b // Unreachable
         }
         aFloating || bFloating -> {
            val floatingOne = if (aFloating) a else b
            val decimalPart = floatingOne.toString().split(".")[1]
            // For 2.0 to equal 2, decimal part must be 0
            // 2.1 would not equal 2
            return decimalPart == "0" && a.toLong() == b.toLong()
         }
         // Both non-floating, so comparing long with long is safe
         else -> a.toLong() == b.toLong()
      }
   }

   private fun isFloating(number: Number): Boolean {
      return number is Float || number is Double
   }

   fun withStrictNumberEquality() = NumberEqualityVerifier(true)
   fun withoutStrictNumberEquality() = NumberEqualityVerifier(false)
}
