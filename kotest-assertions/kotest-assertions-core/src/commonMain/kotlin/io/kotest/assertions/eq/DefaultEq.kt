package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.StringPrint
import io.kotest.assertions.print.print

/**
 * This [Eq] is used when there is no more specific eq available.
 *
 * This implementation will simply compare values using the kotlin == function.
 *
 * It will also convert arrays to lists to support deep comparisons.
 */
internal object DefaultEq : Eq<Any?> {
   override fun equals(actual: Any?, expected: Any?, strictNumberEq: Boolean): Throwable? {
      return if (test(actual, expected)) null else {
         val e = if (expected is String) StringPrint.printUnquoted(expected) else expected.print()
         val a = if (actual is String) StringPrint.printUnquoted(actual) else actual.print()
         AssertionErrorBuilder.create()
            .withValues(Expected(e), Actual(a))
            .build()
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
