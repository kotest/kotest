package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.print

object CollectionEq : Eq<Collection<*>> {

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

   private fun checkSetEquality(actual: Set<*>, expected: Set<*>, context: EqContext): Throwable? {
      return if (actual.size != expected.size) generateError(actual, expected) else {
         val (isEqual, innerError) = equalsIgnoringOrder(actual, expected, context)
         (innerError ?: if (isEqual) null else generateError(actual, expected))
      }
   }

   private fun checkIterableCompatibility(actual: Iterable<*>, expected: Iterable<*>): Throwable? {
      val isCompatible =
         ((actual is Collection) && (expected is Collection))
            || (actual::class.isInstance(expected) && expected::class.isInstance(actual))
      return if (isCompatible) null else errorWithTypeDetails(actual, expected)
   }

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
      return AssertionErrorBuilder.create().withMessage(detailErrorMessage).build()
   }

   private const val DISALLOWED = "$TRIGGER nesting iterator"

   private fun checkEquality(actual: Iterable<*>, expected: Iterable<*>, context: EqContext): Throwable? {

      val iter1 = actual.iterator()
      val iter2 = expected.iterator()
      val elementDifferAtIndex = mutableListOf<Int>()

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
            AssertionErrorBuilder.create()
               .withMessage(detailErrorMessage)
               .withValues(Expected(expected.print()), Actual(actual.print()))
               .build()
         } else null
   }

   private fun generateError(actual: Any, expected: Any): Throwable = AssertionErrorBuilder.create()
      .withValues(Expected(expected.print()), Actual(actual.print()))
      .build()
}

