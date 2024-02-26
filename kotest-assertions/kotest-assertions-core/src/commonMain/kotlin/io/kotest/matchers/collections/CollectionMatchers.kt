package io.kotest.matchers.collections

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector
import io.kotest.assertions.print.print
import io.kotest.assertions.runWithMode
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher

fun <T> existInOrder(vararg ps: (T) -> Boolean): Matcher<Collection<T>?> = existInOrder(ps.asList())

/**
 * Assert that a collections contains a subsequence that matches the given subsequence of predicates, possibly with
 * values in between.
 */
fun <T> existInOrder(predicates: List<(T) -> Boolean>): Matcher<Collection<T>?> = neverNullMatcher { actual ->
   require(predicates.isNotEmpty()) { "predicates must not be empty" }

   var subsequenceIndex = 0
   val actualIterator = actual.iterator()

   while (actualIterator.hasNext() && subsequenceIndex < predicates.size) {
      if (predicates[subsequenceIndex](actualIterator.next())) subsequenceIndex += 1
   }

   MatcherResult(
      subsequenceIndex == predicates.size,
      { "${actual.print().value} did not match the predicates ${predicates.print().value} in order. Predicate at index $subsequenceIndex did not match." },
      { "${actual.print().value} should not match the predicates ${predicates.print().value} in order" }
   )
}

fun <T> haveSize(size: Int): Matcher<Collection<T>> = haveSizeMatcher(size)



fun <T : Comparable<T>> beSorted(): Matcher<List<T>> = sorted()
fun <T : Comparable<T>> sorted(): Matcher<List<T>> = sortedBy { it }

fun <T, E : Comparable<E>> beSortedBy(transform: (T) -> E): Matcher<List<T>> = sortedBy(transform)
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

fun <T : Comparable<T>> beSortedDescending(): Matcher<List<T>> = sortedDescending()
fun <T : Comparable<T>> sortedDescending(): Matcher<List<T>> = sortedDescendingBy { it }

fun <T, E : Comparable<E>> beSortedDescendingBy(transform: (T) -> E): Matcher<List<T>> = sortedDescendingBy(transform)
fun <T, E : Comparable<E>> sortedDescendingBy(transform: (T) -> E): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      val failure =
         value.withIndex().firstOrNull { (i, it) -> i != value.lastIndex && transform(it) < transform(value[i + 1]) }
      val elementMessage = when (failure) {
         null -> ""
         else -> ". Element ${failure.value} at index ${failure.index} was less than element ${value[failure.index + 1]}"
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
 * Asserts that each element in the collection matches its corresponding matcher in [assertions].
 * Elements will be compared sequentially in the order given by the iterators of the collections.
 */
fun <T> matchEach(assertions: List<(T) -> Unit>): Matcher<Collection<T>?> = neverNullMatcher { actual ->
   data class ElementPass(val atIndex: Int)
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
   } + (actual.size..assertions.size - 1).map {
      MatchEachProblem(
         it,
         "No actual element for assertion at index $it"
      )
   }

   MatcherResult(
      problems.isEmpty(),
      {
         "Expected each element to pass its assertion, but found issues at indexes: [${problems.joinToString { it.atIndex.toString() }}]\n\n" +
            "${problems.joinToString(separator = "\n") { "${it.atIndex} => ${it.problem}" }}"
      },
      { "Expected some element to fail its assertion, but all passed." },
   )
}
