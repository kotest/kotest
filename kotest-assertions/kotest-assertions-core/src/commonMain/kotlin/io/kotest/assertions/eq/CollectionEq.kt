package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.Expected
import io.kotest.assertions.print.print

/**
 * An [Eq] typeclass that compares two [Collection]s for equality.
 *
 * The comparison strategy depends on the runtime types:
 *
 * - Two [Set]s are compared order-independently (element membership only), since `{1,2,3}` and
 *   `{3,2,1}` denote the same set.
 * - Non-[Set] collections (such as [List]s) are compared element by element, in iteration order.
 * - A [Set] is compared in iteration order against a non-`Set` iterable only when [isOrderedSet] reports
 *   it has a stable iteration order (a `LinkedHashSet` or single-element set on any platform, plus
 *   `SortedSet` and Java sequenced sets on the JVM); otherwise the comparison is disallowed, because the
 *   outcome would depend on undefined iteration order.
 *
 * Elements are themselves compared deeply via Kotest's [EqCompare], so nested collections, maps, arrays
 * and data classes are matched structurally rather than by reference. Recursion is guarded against
 * cyclic references: an `actual`/`expected` pair already on the comparison stack short-circuits to
 * [EqResult.Success], and [EqContext] enforces a maximum depth.
 *
 * Iterables that are not [Collection]s and are nested inside another such iterable are disallowed: they
 * short-circuit with a [DISALLOWED] message advising the use of custom test code. [ArrayEq] mirrors this
 * mechanism for nested arrays.
 */
object CollectionEq : Eq<Collection<*>> {

   /**
    * Compares [actual] and [expected] for equality, dispatching on their runtime types.
    *
    * Returns early with [EqResult.Success] for reference-identical arguments and for pairs already on
    * the comparison stack (cycle guard). Otherwise the pair is pushed onto [context] for the duration of
    * the comparison so nested calls can detect cycles and enforce the depth limit.
    *
    * @param context carries strict numeric equality, the active [EqResolver] and the recursion/cycle
    *                tracking state. See [EqContext].
    * @return [EqResult.Success] when equal, otherwise an [EqResult.Failure] describing the differences or
    *         reporting a disallowed comparison.
    */
   override fun equals(actual: Collection<*>, expected: Collection<*>, context: EqContext): EqResult {
      if (actual === expected) return EqResult.Success

      if (context.isVisited(actual, expected)) return EqResult.Success

      context.push(actual, expected)
      try {
         return when {
            actual is Set<*> && expected is Set<*> -> EqResult.wrap(checkSetEquality(actual, expected, context))
            isOrderedSet(actual) || isOrderedSet(expected) -> {
               val t = checkIterableCompatibility(actual, expected) ?: checkEquality(actual, expected, context)
               EqResult.wrap(t)
            }

            actual is Set<*> || expected is Set<*> -> EqResult.Failure { errorWithTypeDetails(actual, expected) }
            else -> {
               val t = checkIterableCompatibility(actual, expected) ?: checkEquality(actual, expected, context)
               EqResult.wrap(t)
            }
         }
      } finally {
         context.pop()
      }
   }

   /**
    * Compares two [Set]s order-independently. Differing sizes fail immediately; otherwise membership is
    * checked with [equalsIgnoringOrder]. A disallowed nested comparison surfaced during the scan is
    * propagated as-is, while any other mismatch yields a generic expected/actual error.
    */
   private fun checkSetEquality(actual: Set<*>, expected: Set<*>, context: EqContext): Throwable? {
      return if (actual.size != expected.size) generateError(actual, expected) else {
         val (isEqual, innerError) = equalsIgnoringOrder(actual, expected, context)
         (innerError ?: if (isEqual) null else generateError(actual, expected))
      }
   }

   /**
    * Guards against comparing structurally incompatible iterables. The pair is compatible when both are
    * [Collection]s, or when each is an instance of the other's runtime class. Returns `null` when
    * compatible, otherwise a disallowed-comparison error from [errorWithTypeDetails].
    */
   private fun checkIterableCompatibility(actual: Iterable<*>, expected: Iterable<*>): Throwable? {
      val isCompatible =
         ((actual is Collection) && (expected is Collection))
            || (actual::class.isInstance(expected) && expected::class.isInstance(actual))
      return if (isCompatible) null else errorWithTypeDetails(actual, expected)
   }

   /**
    * Determines whether two [Set]s contain matching elements regardless of order, returning whether every
    * `actual` element has a counterpart in `expected`, together with the first disallowed-comparison error
    * encountered (a message starting with [TRIGGER]).
    *
    * Collection-like elements (sets, maps, collections, arrays and other iterables) are matched with
    * Kotest's [EqCompare] for deep equality and are only paired with expected elements of the same broad
    * kind; scalar elements fall back to plain [Set.contains].
    */
   // when comparing sets we need to consider that {1,2,3} is the same set as {3,2,1}.
   // but we can't just use the built in equality, because it won't work for nested arrays, eg
   // { [1,2,3], 4 } != { [1,2,3], 4 }
   // so we must use Kotest's Eq typeclass.
   // Performance is sensitive so we must be careful to not end up with O(n^2)
   private fun equalsIgnoringOrder(actual: Set<*>, expected: Set<*>, context: EqContext): Pair<Boolean, Throwable?> {

      var innerError: Throwable? = null

      fun equalWithDetection(elementInActualSet: Any?, it: Any?): Boolean {
         return when (val result = EqCompare.compare(elementInActualSet, it, context)) {
            is EqResult.Failure -> {
               val t = result.error()
               if (null == innerError && (t.message?.startsWith(TRIGGER) == true)) innerError = t
               return false
            }
            EqResult.Success -> true
         }
      }

      return Pair(actual.all { elementInActualSet ->
         // if we have a collection type we must use the eq typeclass
         // to ensure we can support deep equals, otherwise we can just compare
         when (elementInActualSet) {
            is Set<*> -> expected.any {
               it is Set<*> && equalWithDetection(elementInActualSet, it)
            }

            is Map<*, *> -> expected.any {
               it is Map<*, *> && equalWithDetection(elementInActualSet, it)
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

   /**
    * Token (`"Disallowed"`) prefixing every message that marks a comparison as disallowed rather than an
    * ordinary mismatch. It is matched directly by [equalsIgnoringOrder] and also forms the start of both
    * [errorWithTypeDetails]'s messages and the [DISALLOWED] nested-iterator prefix. [ArrayEq] declares an
    * identical token.
    */
   const val TRIGGER = "Disallowed"

   /**
    * Builds the disallowed-comparison error for incompatible iterable types, with a message tailored to
    * the cause:
    *
    * - a [Set] compared with something that lacks a stable iteration order,
    * - a [Collection] compared with an incompatible non-`Collection` ("typed contract"),
    * - two otherwise-unrelated bare iterables ("promiscuous iterators").
    *
    * Every variant is prefixed with [TRIGGER].
    */
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
      return AssertionErrorBuilder.create().withMessage(detailErrorMessage).build()
   }

   /**
    * Full prefix of the message raised when a non-[Collection] iterable is found nested inside another
    * such iterable. [checkEquality] matches on this prefix to short-circuit the comparison. Mirrors
    * [ArrayEq]'s constant of the same name, which targets nested arrays.
    */
   private const val DISALLOWED = "$TRIGGER nesting iterator"

   /**
    * Compares two ordered iterables element by element, in order, accruing every difference it can
    * detect: indices whose elements differ, trailing unexpected elements (present in `actual` only) and
    * trailing missing elements (present in `expected` only). For differing data-class pairs a per-property
    * diff is appended, capped at [AssertionsConfig.maxCollectionDiffCount] with a summary of the rest.
    *
    * Elements are compared first by reference, then deeply via [EqCompare]. A non-[Collection] iterable
    * nested inside another (detected via [DISALLOWED]) short-circuits the whole comparison.
    *
    * @return `null` when the iterables are equal, otherwise the built [Throwable].
    */
   private fun checkEquality(actual: Iterable<*>, expected: Iterable<*>, context: EqContext): Throwable? {

      val iter1 = actual.iterator()
      val iter2 = expected.iterator()
      val elementDifferAtIndex = mutableListOf<Int>()
      val elementDiffDetails = mutableListOf<Pair<Int, String>>()

      fun <T> nestedIterator(item: T, oracle: Iterable<*>): String? = item?.let {
         if ((it is Iterable<*>) && (it !is Collection<*>) && (it::class.isInstance(oracle) || oracle::class.isInstance(
               it
            ))
         ) {
            """$DISALLOWED $it (${it::class.simpleName ?: "anonymous"}) within $oracle (${oracle::class.simpleName ?: "anonymous"}); (use custom test code instead)"""
         } else null
      }

      var nestedIteratorError: String? = null
      var accrueDetails = true

      fun setDisallowedState(disallowedMsg: String): Boolean {
         nestedIteratorError = disallowedMsg
         accrueDetails = false
         return true
      }

      fun equalXorDisallowed(result: EqResult): Throwable? {
         return when (result) {
            is EqResult.Failure -> {
               val t = result.error()
               return if (t.message?.startsWith(DISALLOWED) == true) {
                  setDisallowedState(t.message!!)
                  AssertionErrorBuilder.create().withMessage(nestedIteratorError!!).build()
               } else t
            }
            EqResult.Success -> null
         }
      }

      var index = 0
      var unexpectedElementAtIndex: Int? = null
      var missingElementAt: Int? = null

      while (iter1.hasNext()) {
         val a = iter1.next()
         if (iter2.hasNext()) {
            val b = iter2.next()
            val t: Throwable? = when {
               a === b -> null
               nestedIterator(a, actual)?.let { setDisallowedState(it) } == true ->
                  AssertionErrorBuilder.create().withMessage(nestedIteratorError!!).build()

               nestedIterator(b, expected)?.let { setDisallowedState(it) } == true ->
                  AssertionErrorBuilder.create().withMessage(nestedIteratorError!!).build()

               else -> equalXorDisallowed(EqCompare.compare(a, b, context))
            }
            if (!accrueDetails) break
            if (t != null) {
               elementDifferAtIndex.add(index)
               if (isDataClassInstance(a) && isDataClassInstance(b)) {
                  val msg = t.message
                  if (msg != null) {
                     val diffTree = msg.substringBefore("\n\nexpected:<").trimEnd()
                     elementDiffDetails.add(index to diffTree)
                  }
               }
            }
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
         if (elementDiffDetails.isNotEmpty()) {
            append("\nThe following element(s) differ:\n")
            for ((idx, diffMsg) in elementDiffDetails.take(AssertionsConfig.maxCollectionDiffCount.value)) {
               append("index $idx: $diffMsg\n\n")
            }
            if (elementDiffDetails.size > AssertionsConfig.maxCollectionDiffCount.value) {
               append("... and ${elementDiffDetails.size - AssertionsConfig.maxCollectionDiffCount.value} more differences\n")
            }
         }
      }.toString()

      return nestedIteratorError?.let { AssertionErrorBuilder.create().withMessage(it).build() }
         ?: if (detailErrorMessage.isNotBlank()) {
            AssertionErrorBuilder.create()
               .withMessage(detailErrorMessage)
               .withValues(Expected(expected.print()), Actual(actual.print()))
               .build()
         } else null
   }

   /**
    * Builds a bare assertion error carrying only the printed [expected] and [actual] values, with no
    * element-level detail message.
    */
   private fun generateError(actual: Any, expected: Any): Throwable = AssertionErrorBuilder.create()
      .withValues(Expected(expected.print()), Actual(actual.print()))
      .build()
}

