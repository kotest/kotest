package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.print

object ArrayEq : Eq<Array<*>> {

   override fun equals(actual: Array<*>, expected: Array<*>, strictNumberEq: Boolean): Throwable? {
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

      fun equalXorDisallowed(signal: Throwable?): Throwable? = signal?.let {
         if (it.message?.startsWith(DISALLOWED) == true) {
            setDisallowedState(it.message!!)
            AssertionErrorBuilder.create().withMessage(nestedIteratorError!!).build()
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
               nestedIterator(a, actual)?.let { setDisallowedState(it) } == true ->
                  AssertionErrorBuilder.create().withMessage(nestedIteratorError!!).build()

               nestedIterator(b, expected)?.let { setDisallowedState(it) } == true ->
                  AssertionErrorBuilder.create().withMessage(nestedIteratorError!!).build()

               else -> equalXorDisallowed(EqCompare.compare(a, b, strictNumberEq))
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

      return nestedIteratorError?.let { AssertionErrorBuilder.create().withMessage(it).build() }
         ?: if (detailErrorMessage.isNotBlank()) {
            AssertionErrorBuilder.create().withMessage(detailErrorMessage)
               .withValues(Expected(expected.print()), Actual(actual.print()))
               .build()
         } else null
   }

   const val TRIGGER = "Disallowed"

   private const val DISALLOWED = "$TRIGGER nesting array"
}

