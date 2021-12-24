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
         val (isEqual, setInnerError) = equalsIgnoringOrder(actual, expected, strictNumberEq)
         (setInnerError?.let { error ->
            if (error.message?.startsWith(trigger) == true) error /*.message?.replace(atIndex," content ") */ else generateError(actual, expected)
         } ?: if (isEqual) null else generateError(actual, expected))
      }
   }

   private fun checkIterableCompatibility(actual: Iterable<*>, expected: Iterable<*>): Throwable? {
      val isCompatible = (actual is Collection && expected is Collection) ||  (actual::class.isInstance(expected) && expected::class.isInstance(actual))
      return if (isCompatible) null else errorWithTypeDetails(actual,expected)
   }

   // when comparing sets we need to consider that {1,2,3} is the same set as {3,2,1}.
   // but we can't just use the built in equality, because it won't work for nested arrays, eg
   // { [1,2,3], 4 } != { [1,2,3], 4 }
   // so we must use Kotest's Eq typeclass.
   // Performance is sensitive so we must be careful to not end up with O(n^2)
   private fun equalsIgnoringOrder(actual: Set<*>, expected: Set<*>, strictNumberEq: Boolean): Pair<Boolean, Throwable?> {

      var error: Throwable? = null

      fun equalOrAssignToError(elementInActualSet: Any?, it: Any?) =
         eq(elementInActualSet, it, strictNumberEq)?.let {
            error = it
            false
         } ?: true

      return Pair(actual.all { elementInActualSet ->
         // if we have a collection type we must use the eq typeclass
         // to ensure we can support deep equals, otherwise we can just compare
         when (elementInActualSet) {
            is Set<*> -> expected.any { it: Any? ->
               it is Set<*> && equalOrAssignToError(elementInActualSet, it)
            }
            is Array<*> -> expected.any { it ->
               it is Array<*> && equalOrAssignToError(elementInActualSet, it)
            }
            is Map<*,*> -> expected.any { it ->
               it is Map<*,*> && equalOrAssignToError(elementInActualSet, it)
            }
            is Collection<*> -> expected.any { it: Any? ->
               it !is Set<*>
                  && it is Collection<*>
                  && equalOrAssignToError(elementInActualSet, it)
            }
            is Iterable<*> -> expected.any { it: Any? ->
               it !is Set<*>
                  && it !is Collection<*>
                  && it is Iterable<*>
                  && equalOrAssignToError(elementInActualSet, it)
            }
            else -> expected.contains(elementInActualSet)
         }
      }, error)
   }

   const val trigger = "Unsupported"

   private fun errorWithTypeDetails(actual: Iterable<*>, expected: Iterable<*>): Throwable {
      val tag = "${actual::class.simpleName?.let {it} ?: actual::class} with ${expected::class.simpleName?.let {it} ?: expected::class} regardless of content\n"
      val detailErrorMessage = when {
         actual is Set<*> || expected is Set<*> -> "Set can be compared only to Set\nCannot compare $tag"
         actual is Collection || expected is Collection -> "Collection can be compared only to Collection\nCannot compare $tag"
         else -> "$trigger promiscuous iterators\nCannot compare $tag"
      }
      return failure(Expected(Printed("(unsupported)")), Actual(Printed("(unsupported)")), detailErrorMessage)
   }

   private const val unim = "$trigger nesting iterator"

   private fun checkEquality(actual: Iterable<*>, expected: Iterable<*>, strictNumberEq: Boolean): Throwable? {

      val iter1 = actual.iterator()
      val iter2 = expected.iterator()
      val elementDifferAtIndex = mutableListOf<Int>()

      fun <T> nestedIteratorMsg(item: T): String? = item?.let {
         if ((it is Iterable<*>) && (it !is Collection<*>) && (it::class.isInstance(actual) || actual::class.isInstance(it))) {
         """$unim $it (${it::class.simpleName ?: "anonymous" }) within $iter1 (${it::class.simpleName ?: "anonymous" }); (use custom test code instead)"""
      } else null }

      var index = 0
      var unexpectedElementAtIndex: Int? = null
      var missingElementAt: Int? = null
      var nestedIteratorError: String? = null
      var accrueDetails = true

      while (iter1.hasNext()) {
         val a = iter1.next()
         if (iter2.hasNext()) {
            val b = iter2.next()
            val t: Throwable? = when {
               a?.equals(b) == true -> null
               nestedIteratorMsg(a)?.let {
                  nestedIteratorError = it
                  accrueDetails = false
                  true
               } == true -> failure(nestedIteratorError!!)
               nestedIteratorMsg(b)?.let {
                  nestedIteratorError = it
                  accrueDetails = false
                  true
               } == true -> failure(nestedIteratorError!!)
               else -> eq(a, b, strictNumberEq)?.let {
                  if (it.message?.startsWith(unim) == true) {
                     nestedIteratorError = it.message
                     accrueDetails = false
                     failure(nestedIteratorError!!)
                  } else it
               }
            }
            if (t != null && accrueDetails)
               elementDifferAtIndex.add(index)
            else if (!accrueDetails) break
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
