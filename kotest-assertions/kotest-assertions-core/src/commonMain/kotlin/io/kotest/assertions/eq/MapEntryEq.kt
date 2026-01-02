package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.print

/**
 * This [Eq] is used to compare [Map.Entry] instances.
 * It uses an [EqCompare] function to compare the keys and values of the entries.
 */
internal object MapEntryEq : Eq<Map.Entry<*, *>> {

   override fun equals(actual: Map.Entry<*, *>, expected: Map.Entry<*, *>, context: EqContext): EqResult {
      val compareKey = EqCompare.compare(actual.key, expected.key, context)
      val compareValue = EqCompare.compare(actual.value, expected.value, context)
      return if (compareKey.equal && compareValue.equal)
         EqResult.Success
      else
         EqResult.failure {
            AssertionErrorBuilder.create()
               .withValues(Expected(expected.print()), Actual(actual.print()))
               .build()
         }
   }
}
