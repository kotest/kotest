package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.print

/**
 * An [Eq] typeclass that compares two [Array]s for deep, ordered equality.
 *
 * Arrays are compared element by element, in order. Each pair is first checked with the elements'
 * own `equals`, falling back to Kotest's [EqCompare] so that nested structures (collections, maps,
 * data classes, etc.) are themselves compared deeply rather than by reference. The resulting
 * [EqResult.Failure] accrues every difference it can detect:
 *
 * - elements that differ at a given index,
 * - elements present in `actual` but not in `expected` (unexpected, trailing elements),
 * - elements present in `expected` but not in `actual` (missing, trailing elements).
 *
 * Nested arrays are intentionally disallowed: comparing an array nested inside another array does
 * not have well-defined equality semantics here, so such a case short-circuits with a [DISALLOWED]
 * failure message advising the use of custom test code instead. This mirrors the "Disallowed"
 * mechanism in [CollectionEq], which applies the analogous restriction to nested non-`Collection`
 * iterables.
 */
object ArrayEq : Eq<Array<*>> {

   /**
    * Compares [actual] and [expected] for deep, ordered equality.
    *
    * @param context extra context forwarded to nested comparisons, such as strict numeric equality and
    *                recursion/cycle tracking. See [EqContext].
    * @return [EqResult.Success] if the arrays are deeply equal, otherwise an [EqResult.Failure] whose
    *         lazily-built error describes the differing, unexpected and missing indices, or reports a
    *         disallowed nested array.
    */
   override fun equals(actual: Array<*>, expected: Array<*>, context: EqContext): EqResult {

      val iter1 = actual.iterator()
      val iter2 = expected.iterator()
      val elementDifferAtIndex = mutableListOf<Int>()

      fun <T> nestedIterator(item: T, other: Array<*>): String? = item?.let {
         if (it::class.isInstance(other) || other::class.isInstance(it)) {
            """$DISALLOWED $it (${it::class.simpleName ?: "anonymous"}) within $other (${other::class.simpleName ?: "anonymous"}); (use custom test code instead)"""
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
               val e = result.error()
               if (e.message?.startsWith(DISALLOWED) == true) {
                  setDisallowedState(e.message!!)
                  AssertionErrorBuilder.create().withMessage(nestedIteratorError!!).build()
               } else e
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
               a?.equals(b) == true -> null
               nestedIterator(a, actual)?.let { setDisallowedState(it) } == true ->
                  AssertionErrorBuilder.create().withMessage(nestedIteratorError!!).build()

               nestedIterator(b, expected)?.let { setDisallowedState(it) } == true ->
                  AssertionErrorBuilder.create().withMessage(nestedIteratorError!!).build()

               else -> equalXorDisallowed(EqCompare.compare(a, b, context))
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

      if (nestedIteratorError != null) {
         return EqResult.Failure {
            AssertionErrorBuilder.create().withMessage(nestedIteratorError).build()
         }
      }

      return if (detailErrorMessage.isNotBlank()) {
         EqResult.Failure {
            AssertionErrorBuilder.create().withMessage(detailErrorMessage)
               .withValues(Expected(expected.print()), Actual(actual.print()))
               .build()
         }
      } else EqResult.Success
   }

   /**
    * Base token (`"Disallowed"`) marking a failure message as a disallowed comparison rather than an
    * ordinary element difference. Used here only to compose [DISALLOWED], which is the prefix actually
    * matched when these failures are detected and propagated. [CollectionEq] declares its own identical
    * token for the same purpose.
    */
   const val TRIGGER = "Disallowed"

   /**
    * Full prefix of the message raised when an array is found nested inside another array. This is the
    * prefix [equals] matches on to recognise and short-circuit a disallowed nested-array comparison.
    */
   private const val DISALLOWED = "$TRIGGER nesting array"
}

