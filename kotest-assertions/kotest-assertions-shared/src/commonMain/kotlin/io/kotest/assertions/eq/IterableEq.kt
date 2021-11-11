package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.print

object IterableEq : Eq<Iterable<*>> {

   /**
    * Returns true if this [Iterable] is an iterable that is supported by this typeclass.
    *
    * There are many third party libs that use Iterable as a base type (Jackson's Json Node, JDK's Path type, to
    * name but two) and we cannot safely compare these simply using the IterableEq.
    *
    * Therefore only explicit types are accepted by our IterableEq.
    */
   fun isValidIterable(it: Any): Boolean {
      return when (it) {
         is List<*>, is Set<*>, is Array<*>, is Collection<*> -> true
         else -> false
      }
   }

   fun asIterable(it: Any): Iterable<*> {
      return when (it) {
         is Array<*> -> it.asList()
         is List<*> -> it
         is Set<*> -> it
         is Collection<*> -> it
         else -> error("Cannot convert $it to Iterable<*>")
      }
   }

   override fun equals(actual: Iterable<*>, expected: Iterable<*>, strictNumberEq: Boolean): Throwable? {
      return when {
         actual is Set<*> && expected is Set<*> -> checkSetEquality(actual, expected, strictNumberEq)
         else -> checkEquality(actual, expected, strictNumberEq)
      }
   }

   private fun checkSetEquality(actual: Set<*>, expected: Set<*>, strictNumberEq: Boolean): Throwable? {
      return if (actual.size != expected.size || !equalsIgnoringOrder(actual, expected, strictNumberEq)) {
         generateError(actual, expected)
      } else null
   }

   // when comparing sets we need to consider that {1,2,3} is the same set as {3,2,1}.
   // but we can't just use the built in equality, because it won't work for nested arrays, eg
   // { [1,2,3], 4 } != { [1,2,3], 4 }
   // so we must use Kotest's Eq typeclass.
   // Performance is sensitive so we must be careful to not end up with O(n^2)
   private fun equalsIgnoringOrder(actual: Set<*>, expected: Set<*>, strictNumberEq: Boolean): Boolean {
      return actual.all { elementInActualSet ->
         // if we have a collection type we must use the eq typeclass
         // to ensure we can support deep equals, otherwise we can just compare
         when (elementInActualSet) {
            is Iterable<*> -> expected.any { eq(elementInActualSet, it, strictNumberEq) == null }
            is Array<*> -> expected.any { eq(elementInActualSet, it, strictNumberEq) == null }
            else -> expected.contains(elementInActualSet)
         }
      }
   }

   private fun checkEquality(actual: Iterable<*>, expected: Iterable<*>, strictNumberEq: Boolean): Throwable? {
      var index = 0
      val iter1 = actual.iterator()
      val iter2 = expected.iterator()
      val elementDifferAtIndex = mutableListOf<Int>()
      var unexpectedElementAtIndex: Int? = null
      var missingElementAt: Int? = null
      while (iter1.hasNext()) {
         val a = iter1.next()
         if (iter2.hasNext()) {
            val b = iter2.next()
            val t = eq(a, b, strictNumberEq)
            if (t != null) {
               elementDifferAtIndex.add(index)
            }
         } else {
            unexpectedElementAtIndex = index
         }
         index++
      }
      if (iter2.hasNext()) {
         missingElementAt = index
      }
      val detailErrorMessage = StringBuilder().apply {
         if (elementDifferAtIndex.isNotEmpty()) {
            append("Element differ at index: ${elementDifferAtIndex.print().value}\n")
         }
         if (unexpectedElementAtIndex != null) {
            append("Unexpected elements from index $unexpectedElementAtIndex\n")
         }
         if (missingElementAt != null) {
            append("Missing elements from index $missingElementAt\n")
         }
      }.toString()

      return if (detailErrorMessage.isNotBlank()) {
         failure(Expected(expected.print()), Actual(actual.print()), detailErrorMessage)
      } else null
   }

   private fun generateError(actual: Any, expected: Any) = failure(Expected(expected.print()), Actual(actual.print()))
}
