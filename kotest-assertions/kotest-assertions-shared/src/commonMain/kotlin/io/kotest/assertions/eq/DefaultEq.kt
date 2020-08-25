package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.show.show

/**
 * This [Eq] is used when there is no more specific eq available.
 *
 * This implementation will simply compare values using the kotlin == function,
 * and in the case of inequality, delegates to [failure].
 */
internal object DefaultEq : Eq<Any> {
   override fun equals(actual: Any, expected: Any): Throwable? {
      return if (test(actual, expected)) null else {
         failure(Expected(expected.show()), Actual(actual.show()))
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
