package io.kotest.matchers.collections

import io.kotest.assertions.show.show
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
      { "${actual.show().value} did not match the predicates ${predicates.show().value} in order" },
      { "${actual.show().value} should not match the predicates ${predicates.show().value} in order" }
   )
}

fun <T> haveSize(size: Int): Matcher<Collection<T>> = haveSizeMatcher(size)

fun <T> singleElement(t: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = MatcherResult(
     value.size == 1 && value.first() == t,
     { "Collection should be a single element of $t but has ${value.size} elements: ${value.show().value}" },
     { "Collection should not be a single element of $t" }
  )
}

fun <T> singleElement(p: (T) -> Boolean): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {
      val filteredValue: List<T> = value.filter(p)
      return MatcherResult(
         filteredValue.size == 1,
         { "Collection should have a single element by a given predicate but has ${filteredValue.size} elements: ${value.show().value}" },
         { "Collection should not have a single element by a given predicate" }
      )
   }
}

fun <T : Comparable<T>> beSorted(): Matcher<List<T>> = sorted()
fun <T : Comparable<T>> sorted(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    val failure = value.withIndex().firstOrNull { (i, it) -> i != value.lastIndex && it > value[i + 1] }
    val elementMessage = when (failure) {
      null -> ""
      else -> ". Element ${failure.value} at index ${failure.index} was greater than element ${value[failure.index + 1]}"
    }
    return MatcherResult(
       failure == null,
       { "List ${value.show().value} should be sorted$elementMessage" },
       { "List ${value.show().value} should not be sorted" }
    )
  }
}



