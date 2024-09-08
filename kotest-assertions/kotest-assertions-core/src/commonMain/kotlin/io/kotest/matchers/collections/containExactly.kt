package io.kotest.matchers.collections

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.eq.IterableEq
import io.kotest.assertions.eq.eq
import io.kotest.assertions.print.print
import io.kotest.equals.Equality
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.jvm.JvmName
import io.kotest.similarity.possibleMatchesDescription

/**
 * Assert that a collection contains exactly, and only, the given elements, in the same order.
 */
@JvmName("shouldContainExactly_iterable")
infix fun <T> Iterable<T>?.shouldContainExactly(expected: Iterable<T>) =
   this?.toList() should containExactly(expected.toList())

/**
 * Assert that a collection contains exactly, and only, the given elements, in the same order.
 */
@JvmName("shouldContainExactly_array")
infix fun <T> Array<T>?.shouldContainExactly(expected: Array<T>) =
   this?.asList() should containExactly(*expected)

/**
 * Assert that a collection contains exactly, and only, the given elements, in the same order.
 */
fun <T> Iterable<T>?.shouldContainExactly(vararg expected: T) =
   this?.toList() should containExactly(*expected)

/**
 * Assert that a collection contains exactly, and only, the given elements, in the same order.
 */
fun <T> Array<T>?.shouldContainExactly(vararg expected: T) =
   this?.asList() should containExactly(*expected)

/**
 * Assert that a collection contains exactly, and only, the given elements, in the same order.
 */
infix fun <T, C : Collection<T>> C?.shouldContainExactly(expected: C) = this should containExactly(expected)

/**
 * Assert that a collection contains exactly, and only, the given elements, in the same order.
 */
fun <T> Collection<T>?.shouldContainExactly(vararg expected: T) = this should containExactly(*expected)

/**
 * Assert that a collection contains exactly, and only, the given elements, in the same order.
 */
fun <T> containExactly(vararg expected: T): Matcher<Collection<T>?> = containExactly(expected.asList())

/**
 * Assert that a collection contains exactly, and only, the given elements, in the same order.
 */
fun <T, C : Collection<T>> containExactly(expected: C): Matcher<C?> =
   containExactly(expected, null)

/**
 * Assert that a collection contains exactly, and only, the given elements, in the same order.
 */
fun <T, C : Collection<T>> containExactly(
   expected: C,
   verifier: Equality<T>?,
): Matcher<C?> = neverNullMatcher { actual ->
   fun Throwable?.isDisallowedIterableComparisonFailure() =
      this?.message?.startsWith(IterableEq.trigger) == true

   val failureReason = if(verifier == null) {
      eq(actual, expected, strictNumberEq = true)
   } else {
      matchCollectionsWithVerifier(actual, expected, verifier)
   }

   val missing = expected.filterNot { t ->
      actual.any { verifier?.verify(it, t)?.areEqual() ?: (it == t) }
   }
   val extra = actual.filterNot { t ->
      expected.any { verifier?.verify(it, t)?.areEqual() ?: (it == t) }
   }
   val passed = failureReason == null

   val failureMessage = {
      buildString {
         if (failureReason.isDisallowedIterableComparisonFailure()) {
            append(failureReason?.message)
         } else {
            append(
               "Collection should contain exactly: ${expected.print().value} but was: ${actual.print().value}"
            )
            appendLine()
         }

         if (failureReason is CollectionMismatchWithCustomVerifier) {
            append(failureReason.message)
            appendLine()
         }

         appendMissingAndExtra(missing, extra)
         appendLine()
         appendPossibleMatches(missing, expected)

         if (!passed && !failureReason.isDisallowedIterableComparisonFailure() && verifier == null) {
            appendSubmatches(actual, expected)
         }
      }
   }

   val negatedFailureMessage =
      { "Collection should not contain exactly: ${expected.print().value}" }

   if (failureReason.isDisallowedIterableComparisonFailure()) {
      MatcherResult(
         passed,
         failureMessage,
         negatedFailureMessage,
      )
   } else if (
      actual.size > AssertionsConfig.maxCollectionEnumerateSize &&
      expected.size > AssertionsConfig.maxCollectionEnumerateSize
   ) {
      MatcherResult(
         passed,
         {
            failureMessage() + "(set the 'kotest.assertions.collection.enumerate.size' JVM property to see full output)"
         },
         {
            negatedFailureMessage() + "(set the 'kotest.assertions.collection.enumerate.size' JVM property to see full output)"
         },
      )
   } else {
      ComparableMatcherResult(
         passed,
         failureMessage,
         negatedFailureMessage,
         actual.print().value,
         expected.print().value,
      )
   }
}

internal fun<T> matchCollectionsWithVerifier(
   actual: Collection<T>,
   expected: Collection<T>,
   verifier: Equality<T>
): CollectionMismatchWithCustomVerifier? {
   val actualIterator = actual.iterator()
   val expectedIterator = expected.iterator()
   var index = 0
   while (actualIterator.hasNext()) {
      val actualElement = actualIterator.next()
      if (expectedIterator.hasNext()) {
         val expectedElement = expectedIterator.next()
         val equalityResult = verifier.verify(actualElement, expectedElement)
         if(!equalityResult.areEqual()) {
            return CollectionMismatchWithCustomVerifier(
               "Elements differ at index $index, expected: <${expectedElement.print().value}>, but was <${actualElement.print().value}>, ${equalityResult.details().explain()}"
            )
         }
      } else {
         return CollectionMismatchWithCustomVerifier("Actual has an element at index $index, expected is shorter")
      }
      index++
   }
   if (expectedIterator.hasNext()) {
      return CollectionMismatchWithCustomVerifier("Expected has an element at index $index, actual is shorter")
   }
   return null
}

internal class CollectionMismatchWithCustomVerifier(message: String): Exception(message)

@JvmName("shouldNotContainExactly_iterable")
infix fun <T> Iterable<T>?.shouldNotContainExactly(expected: Iterable<T>) =
   this?.toList() shouldNot containExactly(expected.toList())

@JvmName("shouldNotContainExactly_array")
infix fun <T> Array<T>?.shouldNotContainExactly(expected: Array<T>) = this?.asList() shouldNot containExactly(*expected)

fun <T> Iterable<T>?.shouldNotContainExactly(vararg expected: T) = this?.toList() shouldNot containExactly(*expected)
fun <T> Array<T>?.shouldNotContainExactly(vararg expected: T) = this?.asList() shouldNot containExactly(*expected)

infix fun <T, C : Collection<T>> C?.shouldNotContainExactly(expected: C) = this shouldNot containExactly(expected)
fun <T> Collection<T>?.shouldNotContainExactly(vararg expected: T) = this shouldNot containExactly(*expected)

fun StringBuilder.appendMissingAndExtra(missing: Collection<Any?>, extra: Collection<Any?>) {
   if (missing.isNotEmpty()) {
      append("Some elements were missing: ${missing.take(AssertionsConfig.maxCollectionPrintSize.value).print().value}")
   }
   if (missing.isNotEmpty() && extra.isNotEmpty()) {
      append(
         " and some elements were unexpected: ${
            extra.take(AssertionsConfig.maxCollectionPrintSize.value).print().value
         }"
      )
   }
   if (missing.isEmpty() && extra.isNotEmpty()) {
      append(
         "Some elements were unexpected: ${
            extra.take(AssertionsConfig.maxCollectionPrintSize.value).print().value
         }"
      )
   }
}

internal fun<T> StringBuilder.appendPossibleMatches(missing: Collection<T>, expected: Collection<T>) {
   val possibleMatches = missing
      .map { possibleMatchesDescription(expected.toSet(), it) }
      .filter { it.isNotEmpty() }
   if(possibleMatches.isNotEmpty()) {
      append("\nPossible matches:\n${possibleMatches.take(AssertionsConfig.maxSimilarityPrintSize.value).joinToString("\n\n")}")
   }
   if(AssertionsConfig.maxSimilarityPrintSize.value < possibleMatches.size) {
      append("\nPrinted first ${AssertionsConfig.maxSimilarityPrintSize.value} similarities out of ${possibleMatches.size}, (set the 'kotest.assertions.similarity.print.size' JVM property to see full output for similarity)\n")
   }
}

private fun <T> StringBuilder.appendSubmatches(actual: Collection<T>, expected: Collection<T>) {
   val (partialMatchesList, partialMatchesDescription) = describePartialMatchesInCollection(expected, actual.toList())
   if (partialMatchesList.isNotEmpty()) {
      appendLine()
      appendLine(partialMatchesList)
      appendLine(partialMatchesDescription)
   }
}
