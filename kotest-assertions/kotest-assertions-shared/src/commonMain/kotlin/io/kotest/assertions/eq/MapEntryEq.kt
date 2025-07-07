package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.print

internal object MapEntryEq : Eq<Map.Entry<*, *>> {
   override fun equals(actual: Map.Entry<*, *>, expected: Map.Entry<*, *>, strictNumberEq: Boolean): Throwable? {
      return if (EqCompare.compare(actual.key, expected.key, false) == null &&
         EqCompare.compare(actual.value, expected.value, false) == null
      ) null else {
         failure(Expected(expected.print()), Actual(actual.print()))
      }
   }
}
