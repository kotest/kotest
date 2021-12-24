package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print

object IterableEq : Eq<Iterable<*>> {

   fun isValidIterable(it: Any): Boolean {
      return when (it) {
         is String -> false
         is List<*>, is Set<*>, is Array<*>, is Collection<*>, is Iterable<*> -> true
         else -> false
      }
   }

   fun asIterable(it: Any): Iterable<*> {
      check(it !is String)
      return when (it) {
         is Array<*> -> it.asList()
         is List<*> -> it
         is Set<*> -> it
         is Collection<*> -> it
         is Iterable<*> -> it
         else -> error("Cannot convert $it to Iterable<*>")
      }
   }

   override fun equals(actual: Iterable<*>, expected: Iterable<*>, strictNumberEq: Boolean): Throwable? {
      return when {
         actual is Set<*> && expected is Set<*> -> checkSetEquality(actual, expected, strictNumberEq)
         actual is Set<*> || expected is Set<*> -> errorWithTypeDetails(actual, expected)
         else -> {
            checkIterableCompatibility(actual, expected) ?: checkEquality(actual, expected, strictNumberEq)
         }
      }
   }

   private fun checkSetEquality(actual: Set<*>, expected: Set<*>, strictNumberEq: Boolean): Throwable? {
      return if (actual.size != expected.size) generateError(actual, expected) else {
         val (isEqual, innerError) = equalsIgnoringOrder(actual, expected, strictNumberEq)
         (innerError ?: if (isEqual) null else generateError(actual, expected))
      }
   }

   private fun checkIterableCompatibility(actual: Iterable<*>, expected: Iterable<*>): Throwable? {
      val isCompatible =
         ((actual is Collection || actual is Array<*>) && (expected is Collection) || (expected is Array<*>))
            || (actual::class.isInstance(expected) && expected::class.isInstance(actual))
      return if (isCompatible) null else errorWithTypeDetails(actual,expected)
   }

   // when comparing sets we need to consider that {1,2,3} is the same set as {3,2,1}.
   // but we can't just use the built in equality, because it won't work for nested arrays, eg
   // { [1,2,3], 4 } != { [1,2,3], 4 }
   // so we must use Kotest's Eq typeclass.
   // Performance is sensitive so we must be careful to not end up with O(n^2)
   private fun equalsIgnoringOrder(actual: Set<*>, expected: Set<*>, strictNumberEq: Boolean): Pair<Boolean, Throwable?> {

      var innerError: Throwable? = null

      fun equalWithDetection(elementInActualSet: Any?, it: Any?) =
         eq(elementInActualSet, it, strictNumberEq)?.let {
            if (null == innerError && (it.message?.startsWith(trigger) == true)) innerError = it
            false
         } ?: true

      return Pair(actual.all { elementInActualSet ->
         // if we have a collection type we must use the eq typeclass
         // to ensure we can support deep equals, otherwise we can just compare
         when (elementInActualSet) {
            is Set<*> -> expected.any {
               it is Set<*> && equalWithDetection(elementInActualSet, it)
            }
            is Map<*,*> -> expected.any {
               it is Map<*,*> && equalWithDetection(elementInActualSet, it)
            }
            is Collection<*>, is Array<*> -> expected.any {
               it !is Set<*>
                  && (it is Collection<*> || it is Array<*>)
                  && equalWithDetection(elementInActualSet, it)
            }
            is Iterable<*> -> expected.any {
               it !is Set<*>
                  && it !is Collection<*>
                  && it !is Array<*>
                  && it is Iterable<*>
                  && equalWithDetection(elementInActualSet, it)
            }
            else -> expected.contains(elementInActualSet)
         }
      }, innerError)
   }

   const val trigger = "Disallowed"

   private fun errorWithTypeDetails(actual: Iterable<*>, expected: Iterable<*>): Throwable {
      val tag = "${actual::class.simpleName?.let {it} ?: actual::class} with ${expected::class.simpleName?.let {it} ?: expected::class}\n"
      val detailErrorMessage = when {
         actual is Set<*> || expected is Set<*> -> "$trigger: Set can be compared only to Set\nMay not compare $tag"
         (actual is Collection || actual is Array<*>) || (expected is Collection || expected is Array<*>) -> "$trigger typed contract\nMay not compare $tag"
         else -> "$trigger promiscuous iterators\nMay not compare $tag"
      }
      return failure(Expected(Printed("*")), Actual(Printed("*")), detailErrorMessage)
   }

   private const val disallowed = "$trigger nesting iterator"

   private fun checkEquality(actual: Iterable<*>, expected: Iterable<*>, strictNumberEq: Boolean): Throwable? {

      val iter1 = actual.iterator()
      val iter2 = expected.iterator()
      val elementDifferAtIndex = mutableListOf<Int>()

      fun <T> nestedIterator(item: T): String? = item?.let {
         if ((it is Iterable<*>) && (it !is Collection<*>) && (it::class.isInstance(actual) || actual::class.isInstance(it))) {
         """$disallowed $it (${it::class.simpleName ?: "anonymous" }) within $iter1 (${it::class.simpleName ?: "anonymous" }); (use custom test code instead)"""
      } else null }

      var nestedIteratorError: String? = null
      var accrueDetails = true

      fun setDisallowedState(disallowedMsg: String): Boolean {
         nestedIteratorError = disallowedMsg
         accrueDetails = false
         return true
      }

      fun equalXorDisallowed(signal: Throwable?): Throwable? = signal?.let {
         if (it.message?.startsWith(disallowed) == true) {
            setDisallowedState(it.message!!)
            failure(nestedIteratorError!!)
         } else it
      }

      var index = 0
      var unexpectedElementAtIndex: Int? = null
      var missingElementAt: Int? = null

      while (iter1.hasNext()) {
         val a = iter1.next()
         if (iter2.hasNext()) {
            val b = iter2.next()
            val t: Throwable? = when {
               a?.equals(b) == true -> null
               nestedIterator(a)?.let { setDisallowedState(it) } == true -> failure(nestedIteratorError!!)
               nestedIterator(b)?.let { setDisallowedState(it) } == true -> failure(nestedIteratorError!!)
               else -> equalXorDisallowed(eq(a, b, strictNumberEq))
            }
            if (!accrueDetails) break
            if (t != null) elementDifferAtIndex.add(index)
         } else unexpectedElementAtIndex = index
         index++
      }
      if (iter2.hasNext() && accrueDetails) {
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

      return nestedIteratorError?.let { failure(it) } ?: if (detailErrorMessage.isNotBlank()) {
         failure(Expected(expected.print()), Actual(actual.print()), detailErrorMessage)
      } else null
   }

   private fun generateError(actual: Any, expected: Any) = failure(Expected(expected.print()), Actual(actual.print()))
}
