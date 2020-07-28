package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.show.show

object IterableEq : Eq<Iterable<*>> {
   override fun equals(actual: Iterable<*>, expected: Iterable<*>): Throwable? {
      return when {
         actual === expected -> null
         actual is Set<*> && expected is Set<*> -> checkSetEquality(actual, expected)
         else -> checkIterableEquality(actual, expected)
      }
   }

   private fun checkSetEquality(actual: Set<*>, expected: Set<*>): Throwable? {
      return if (actual.size != expected.size || !equalsIgnoringOrder(actual, expected)) {
         generateError(actual, expected)
      } else null
   }

   private fun equalsIgnoringOrder(actual: Set<*>, expected: Set<*>): Boolean {
      return actual.all { elementInActualSet ->
         expected.any { elementInExpectedSet ->
            eq(elementInActualSet, elementInExpectedSet) == null
         }
      }
   }

   private fun checkIterableEquality(actual: Iterable<*>, expected: Iterable<*>): Throwable? {
      val actualAsIterator = actual.iterator()
      val expectedAsIterator = expected.iterator()

      while (actualAsIterator.hasNext() && expectedAsIterator.hasNext()) {
         val exception = eq(actualAsIterator.next(), expectedAsIterator.next())
         if (exception != null) {
            return generateError(actual, expected)
         }
      }

      return if (actualAsIterator.hasNext() || expectedAsIterator.hasNext()) {
         generateError(actual, expected)
      } else null
   }

   private fun generateError(actual: Any, expected: Any) = failure(Expected(expected.show()), Actual(actual.show()))
}
