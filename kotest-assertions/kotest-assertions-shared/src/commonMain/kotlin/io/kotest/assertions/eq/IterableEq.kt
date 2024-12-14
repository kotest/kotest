package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.print

object IterableEq : Eq<Iterable<*>> {

   /**
    * Kotlin [Iterable] provides a traversal mechanism for structures with guaranteed unambiguous ordering (e.g. [List],
    * [Array]) as well as for structures where ordering is absent or, if provided, is implementation dependent (e.g.
    * [Set], [Map]).  In addition, all [Collection] implementations are [Iterable] by inheritance.  Equality between
    * [Iterable] items is ambiguous on account of the uncertainty of the ordering contract on the ensuing traversal.
    * It stands that [Iterable] equality between ordered and unordered structures, albeit allowed in Kotlin, is
    * fragile and benefits from best-practice conventions.  For instance, in the case of [Set], optional ordering--if
    * provided by the implementation--is semantically ambiguous, too.  Consider for instance [LinkedHashSet],
    * the default implementation of [Set] in Kotlin, where ordering represents the arbitrary order of element
    * insertion: at least the jvm environment provides an alternative for certain [Set] implementations where
    * ordering is instead the collation order of the elements.  On account of fragility, and in the interest
    * of deterministic test outcomes, Kotest allows equality testing between [Actual] and [Expected] [Iterable]s with
    * guaranteed unambiguous ordering, between [Actual] and [Expected] [Iterable]s with no ordering guarantee (in which
    * case, equality executes as an unordered containment test), and between ordered [Iterable]s and [LinkedHashSet]
    * since the latter is ubiquitous and, by implementation, ordered by chronological insertion.
    *  Even though it uses the term [Iterable] but it does not mean that it support equality check for [Iterable]s
    * of any type. It only supports equality check for [Iterable]s of the following types:
    * [List], [Set], [Collection]
    * and additionally [Array]s of any type.
    *
    * An equality comparison executed between disallowed types will result in a best-effort error message with some
    * detail on the reason why it is disallowed. However, if type information is lost, the failure diagnostic may
    * unfortunately become confusing.  If so (e.g. the equality test fails, but the error message seems to contradict
    * the failure), check your types: failure in that case may be from an attempt to compare types for which the test is
    * fragile. Alternatively, instead of
    * ```
    * lhs shouldBe rhs
    * ```
    * use your own specialization of  `equals()` as in
    * ```
    * lhs.equals(rhs) shouldBe true
    * ```
    * if your equality test has ordering requirements that are unique or different from Kotest defaults.
    * */
   fun isValidIterable(it: Any): Boolean {
      return when (it) {
         is String -> false
         is List<*>, is Set<*>, is Array<*>, is Collection<*> -> true
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
         else -> error("Cannot convert $it to Iterable<*>")
      }
   }

   override fun equals(actual: Iterable<*>, expected: Iterable<*>, strictNumberEq: Boolean): Throwable? {
      return when {
         actual is Set<*> && expected is Set<*> -> checkSetEquality(actual, expected, strictNumberEq)
         isOrderedSet(actual) || isOrderedSet(expected) -> {
            checkIterableCompatibility(actual, expected) ?: checkEquality(actual, expected, strictNumberEq)
         }
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
         ((actual is Collection) && (expected is Collection))
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
            if (null == innerError && (it.message?.startsWith(TRIGGER) == true)) innerError = it
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

   const val TRIGGER = "Disallowed"

   private fun errorWithTypeDetails(actual: Iterable<*>, expected: Iterable<*>): Throwable {
      val actualTypeName = actual::class.simpleName ?: actual::class
      val expectedTypeName = expected::class.simpleName ?: expected::class
      val tag = "$actualTypeName with $expectedTypeName\n"

      val detailErrorMessage = when {
         actual is Set<*> || expected is Set<*> -> {
            val (setType, nonSetType) =
               if (actual is Set<*>) actualTypeName to expectedTypeName
               else expectedTypeName to actualTypeName

            "$TRIGGER: Sets can only be compared to sets, unless both types provide a stable iteration order.\n$setType does not provide a stable iteration order and was compared with $nonSetType which is not a Set"
         }
         (actual is Collection) || (expected is Collection) -> "$TRIGGER typed contract\nMay not compare $tag"
         else -> "$TRIGGER promiscuous iterators\nMay not compare $tag"
      }
      return failure(detailErrorMessage)
   }

   private const val DISALLOWED = "$TRIGGER nesting iterator"

   private fun checkEquality(actual: Iterable<*>, expected: Iterable<*>, strictNumberEq: Boolean): Throwable? {

      val iter1 = actual.iterator()
      val iter2 = expected.iterator()
      val elementDifferAtIndex = mutableListOf<Int>()

      fun <T> nestedIterator(item: T, oracle: Iterable<*>): String? = item?.let {
         if ((it is Iterable<*>) && (it !is Collection<*>) && (it::class.isInstance(oracle) || oracle::class.isInstance(it))) {
         """$DISALLOWED $it (${it::class.simpleName ?: "anonymous" }) within $oracle (${oracle::class.simpleName ?: "anonymous" }); (use custom test code instead)"""
      } else null }

      var nestedIteratorError: String? = null
      var accrueDetails = true

      fun setDisallowedState(disallowedMsg: String): Boolean {
         nestedIteratorError = disallowedMsg
         accrueDetails = false
         return true
      }

      fun equalXorDisallowed(signal: Throwable?): Throwable? = signal?.let {
         if (it.message?.startsWith(DISALLOWED) == true) {
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
               nestedIterator(a, actual)?.let { setDisallowedState(it) } == true -> failure(nestedIteratorError!!)
               nestedIterator(b, expected)?.let { setDisallowedState(it) } == true -> failure(nestedIteratorError!!)
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

