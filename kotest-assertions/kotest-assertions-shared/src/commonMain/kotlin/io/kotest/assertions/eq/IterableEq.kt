package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.show.show

object IterableEq : Eq<Iterable<*>> {
   override fun equals(actual: Iterable<*>, expected: Iterable<*>): Throwable? {
      val actualAsList = actual.toList()
      val expectedAsList = expected.toList()

      return if(actualAsList.size != expectedAsList.size) {
         failure(Expected(expected.show()), Actual(actual.show()))
      } else {
         val anyNotEqualElements = actualAsList.zip(expectedAsList).any { eq(it.first, it.second) != null }
         if(anyNotEqualElements) failure(Expected(expected.show()), Actual(actual.show()))
         else null
      }
   }
}
