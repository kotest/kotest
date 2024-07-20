package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.eq.EqResult.Equal
import io.kotest.assertions.eq.EqResult.NotEqual
import io.kotest.assertions.failure
import io.kotest.assertions.print.print

internal object MapEntryEq : Eq<Map.Entry<*, *>> {
   override fun equals(actual: Map.Entry<*, *>, expected: Map.Entry<*, *>, strictNumberEq: Boolean): EqResult {
      return and(eq(actual.key, expected.key), eq(actual.value, expected.value)).mapNotEqual {
         failure(Expected(expected.print()), Actual(actual.print()))
      }
   }
}
