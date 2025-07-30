package io.kotest.assertions.eq

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.print.print

/**
 * An [Eq] for comparing a value to null.
 */
internal object NullEq : Eq<Any?> {

   override fun equals(actual: Any?, expected: Any?, strictNumberEq: Boolean): Throwable? {
      // need this to test for overriden equals methods
      val eq = actual == expected
      val neq = !eq
      return when {
         actual == null && expected == null -> null
         actual == null && expected != null && neq -> actualIsNull(expected)
         actual != null && expected == null && neq -> expectedIsNull(actual)
         actual != expected -> error("[$NullEq] should not be used when both values are not null")
         else -> null
      }
   }

   fun actualIsNull(expected: Any): AssertionError {
      return AssertionErrorBuilder.create()
         .withMessage("Expected ${expected.print().value} but actual was null")
         .build()
   }

   fun expectedIsNull(actual: Any): AssertionError {
      return AssertionErrorBuilder.create()
         .withMessage("Expected null but actual was ${actual.print().value}")
         .build()
   }
}
