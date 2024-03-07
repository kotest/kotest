package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.ActualWithType
import io.kotest.assertions.Expected
import io.kotest.assertions.ExpectedWithType
import io.kotest.assertions.failure
import io.kotest.assertions.failureWithTypeInformation
import io.kotest.assertions.print.print
import io.kotest.assertions.print.printWithType

internal object MapEntryEq : Eq<Map.Entry<*, *>> {
   override fun equals(actual: Map.Entry<*, *>, expected: Map.Entry<*, *>, strictNumberEq: Boolean): Throwable? {
      return if (eq(actual.key, expected.key) == null && eq(actual.value, expected.value) == null) null else {
         failure(Expected(expected.print()), Actual(actual.print()))
      }
   }
}
