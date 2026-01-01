package io.kotest.matchers.collections

import io.kotest.assertions.collector.runWithMode
import io.kotest.assertions.print.print
import io.kotest.matchers.ErrorCollectionMode
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.errorCollector
import io.kotest.matchers.neverNullMatcher

fun <T> haveSize(size: Int): Matcher<Collection<T>> = haveSizeMatcher(size)

/**
 * Note that if `this` is empty, this assertion will pass.
 * because there are no elements in it that _do not_ fail the test.
 *
 * See a more detailed explanation of this logic concept in ["Vacuous truth"](https://en.wikipedia.org/wiki/Vacuous_truth) article.
 */
fun <T : Comparable<T>> beSorted(): Matcher<List<T>> = sorted()

/**
 * Note that if `this` is empty, this assertion will pass.
 * because there are no elements in it that _do not_ fail the test.
 *
 * See a more detailed explanation of this logic concept in ["Vacuous truth"](https://en.wikipedia.org/wiki/Vacuous_truth) article.
 */
fun <T : Comparable<T>> sorted(): Matcher<List<T>> = sortedBy { it }

/**
 * Note that if `this` is empty, this assertion will pass.
 * because there are no elements in it that _do not_ fail the test.
 *
 * See a more detailed explanation of this logic concept in ["Vacuous truth"](https://en.wikipedia.org/wiki/Vacuous_truth) article.
 */
fun <T, E : Comparable<E>> beSortedBy(transform: (T) -> E): Matcher<List<T>> = sortedBy(transform)

/**
 * Note that if `this` is empty, this assertion will pass.
 * because there are no elements in it that _do not_ fail the test.
 *
 * See a more detailed explanation of this logic concept in ["Vacuous truth"](https://en.wikipedia.org/wiki/Vacuous_truth) article.
 */
fun <T, E : Comparable<E>> sortedBy(transform: (T) -> E): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      val failure =
         value.withIndex().firstOrNull { (i, it) -> i != value.lastIndex && transform(it) > transform(value[i + 1]) }
      val elementMessage = when (failure) {
         null -> ""
         else -> ". Element ${failure.value} at index ${failure.index} was greater than element ${value[failure.index + 1]}"
      }
      return MatcherResult(
         failure == null,
         { "List ${value.print().value} should be sorted$elementMessage" },
         { "List ${value.print().value} should not be sorted" }
      )
   }
}

fun <T> matchEach(vararg fns: (T) -> Unit): Matcher<Collection<T>?> = matchEach(fns.asList())
fun <T> matchInOrder(vararg fns: (T) -> Unit): Matcher<Collection<T>?> = matchInOrder(fns.asList(), allowGaps = false)
fun <T> matchInOrderSubset(vararg fns: (T) -> Unit): Matcher<Collection<T>?> =
   matchInOrder(fns.asList(), allowGaps = true)

/**
 * Assert that a [Collection] contains a subsequence that matches the given assertions. Failing elements may occur
 * between passing ones, if [allowGaps] is set to true
 */
fun <T> matchInOrder(assertions: List<(T) -> Unit>, allowGaps: Boolean): Matcher<Collection<T>?> =
   neverNullMatcher { actual ->
      val originalMode = errorCollector.getCollectionMode()
      try {
         data class MatchInOrderSubsetProblem(
            val atIndex: Int,
            val problem: String,
         )

         data class MatchInOrderSubsetResult(
            val startIndex: Int,
            val elementsPassed: Int,
            val problems: List<MatchInOrderSubsetProblem>
         )

         val actualAsList = actual.toList()

         var allPassed = false
         var bestResult: MatchInOrderSubsetResult? = null

         for (startIndex in 0..(actual.size - assertions.size)) {
            var elementsPassed = 0
            var elementsTested = 0
            val currentProblems = ArrayList<MatchInOrderSubsetProblem>()

            while (startIndex + elementsTested < actual.size) {
               if (bestResult == null || elementsPassed > bestResult.elementsPassed) {
                  bestResult = MatchInOrderSubsetResult(startIndex, elementsPassed, currentProblems)
               }

               if (!allowGaps && elementsTested > elementsPassed) break

               val elementResult = runCatching {
                  assertions[elementsPassed](actualAsList[startIndex + elementsTested])
               }

               if (elementResult.isSuccess) {
                  elementsPassed++
                  currentProblems.clear()
                  if (elementsPassed == assertions.size) {
                     allPassed = true
                     break
                  }
               } else {
                  currentProblems.add(
                     MatchInOrderSubsetProblem(
                        startIndex + elementsTested,
                        elementResult.exceptionOrNull()!!.message!!
                     )
                  )
               }

               elementsTested++
            }

            if (allPassed) break
         }

         MatcherResult(
            allPassed,
            {
               """
            |Expected a sequence of elements to pass the assertions, ${if (allowGaps) "possibly with gaps between " else ""}but failed to match all assertions
            |
            |Best result when comparing from index [${bestResult?.startIndex}], where ${bestResult?.elementsPassed} elements passed, but the following elements failed:
            |
            ${
                  bestResult?.problems?.joinToString("\n") { problem ->
                     "|${problem.atIndex} => ${problem.problem}"
                  }
               }
            """.trimMargin()
            },
            { "Expected some assertion to fail but all passed" }
         )
      } finally {
         errorCollector.setCollectionMode(originalMode)
      }
   }

/**
 * Asserts that each element of the collection matches with the element of [expected].
 * Elements will be compared sequentially by passing the actual / expected pairs to the
 * [asserter] in the order given by the iterators of the collections.
 */
fun <T> matchEach(expected: List<T>, asserter: (T, T) -> Unit): Matcher<Collection<T>?> =
   matchEach(expected.map { expectedElement ->
      { actualElement: T ->
         asserter(actualElement, expectedElement)
      }
   })

/**
 * Asserts that each element in the collection matches its corresponding matcher in [assertions].
 * Elements will be compared sequentially in the order given by the iterators of the collections.
 */
fun <T> matchEach(assertions: List<(T) -> Unit>): Matcher<Collection<T>?> = neverNullMatcher { actual ->
   data class MatchEachProblem(val atIndex: Int, val problem: String?)

   val problems = errorCollector.runWithMode(ErrorCollectionMode.Hard) {
      actual.mapIndexedNotNull { index, element ->
         if (index !in assertions.indices) {
            MatchEachProblem(
               index,
               "Element has no corresponding assertion. Only ${assertions.size} assertions provided"
            )
         } else {
            runCatching {
               assertions[index](element)
            }.exceptionOrNull()?.let { exception ->
               MatchEachProblem(index, exception.message)
            }
         }
      }
   } + (actual.size until assertions.size).map {
      MatchEachProblem(
         it,
         "No actual element for assertion at index $it"
      )
   }

   MatcherResult(
      problems.isEmpty(),
      {
         "Expected each element to pass its assertion, but found issues at indexes: [${problems.joinToString { it.atIndex.toString() }}]\n\n" +
            problems.joinToString(separator = "\n") { "${it.atIndex} => ${it.problem}" }
      },
      { "Expected some element to fail its assertion, but all passed." },
   )
}
