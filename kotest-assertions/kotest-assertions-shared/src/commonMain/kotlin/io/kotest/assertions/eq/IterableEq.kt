package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.show.show

object IterableEq : Eq<Iterable<*>> {
   override fun equals(actual: Iterable<*>, expected: Iterable<*>): Throwable? {
      return when {
         actual is Set<*> && expected is Set<*> -> checkSetEquality(actual, expected)
         else -> checkIterableEquality(actual, expected)
      }
   }

   private fun checkSetEquality(actual: Set<*>, expected: Set<*>): Throwable? {
      return if (actual.size != expected.size || !equalsIgnoringOrder(actual, expected)) {
         generateError(actual, expected)
      } else null
   }

   // when comparing sets we need to consider that {1,2,3} is the same set as {3,2,1}.
   // but we can't just use the built in equality, because it won't work for nested arrays, eg
   // { [1,2,3], 4 } != { [1,2,3], 4 }
   // so we must use Kotest's Eq typeclass.
   // Performance is sensitive so we must be careful to not end up with O(n^2)
   private fun equalsIgnoringOrder(actual: Set<*>, expected: Set<*>): Boolean {
      return actual.all { elementInActualSet ->
         // if we have a collection type we must use the eq typeclass
         // to ensure we can support deep equals, otherwise we can just compare
         when (elementInActualSet) {
            is Iterable<*> -> expected.any { eq(elementInActualSet, it) == null }
            is Array<*> -> expected.any { eq(elementInActualSet, it) == null }
            else -> expected.contains(elementInActualSet)
         }
      }
   }

   private fun checkIterableEquality(actual: Iterable<*>, expected: Iterable<*>): Throwable? {
      var index = 0
      val iter1 = actual.iterator()
      val iter2 = expected.iterator()
      while (iter1.hasNext()) {
         val a = iter1.next()
         if (iter2.hasNext()) {
            val b = iter2.next()
            val t = eq(a, b)
            if (t != null) return failure(
               Expected(b.show()),
               Actual(a.show()),
               "Elements differ at index $index: ",
            )
         } else {
            return failure("Unexpected element at index $index: ${a.show().value}")
         }
         index++
      }
      if (iter2.hasNext()) {
         val b = iter2.next()
         return failure("Element ${b.show().value} expected at index $index but there were no further elements")
      }
      return null
   }

   private fun generateError(actual: Any, expected: Any) = failure(Expected(expected.show()), Actual(actual.show()))
}
