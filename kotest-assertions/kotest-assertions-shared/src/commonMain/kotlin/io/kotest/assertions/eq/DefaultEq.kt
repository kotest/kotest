package io.kotest.assertions.eq

import io.kotest.assertions.ActualWithType
import io.kotest.assertions.ExpectedWithType
import io.kotest.assertions.failure
import io.kotest.assertions.failureWithTypeInformation
import io.kotest.assertions.print.printWithType

/**
 * This [Eq] is used when there is no more specific eq available.
 *
 * This implementation will simply compare values using the kotlin == function,
 * and in the case of inequality, delegates to [failure] to create an error message.
 */
internal object DefaultEq : Eq<Any?> {
   override fun equals(actual: Any?, expected: Any?, strictNumberEq: Boolean): Throwable? {
      return if (test(actual, expected)) null else {
         failureWithTypeInformation(ExpectedWithType(expected.printWithType()), ActualWithType(actual.printWithType()))
      }
   }

   private fun test(a: Any?, b: Any?): Boolean {
      return makeComparable(a) == makeComparable(b)
   }
}

internal fun makeComparable(any: Any?): Any? {
   return when (any) {
      is BooleanArray -> any.asList()
      is IntArray -> any.asList()
      is ShortArray -> any.asList()
      is FloatArray -> any.asList()
      is DoubleArray -> any.asList()
      is LongArray -> any.asList()
      is ByteArray -> any.asList()
      is CharArray -> any.asList()
      is Array<*> -> any.asList()
      else -> any
   }
}
