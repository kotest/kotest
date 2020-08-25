package io.kotest.matchers.collections

import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher

fun <T> haveSizeMatcher(size: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
    MatcherResult(
      value.size == size,
      { "Collection should have size $size but has size ${value.size}. Values: ${value.show().value}" },
      { "Collection should not have size $size. Values: ${value.show().value}" }
    )
}


fun <T> beEmpty(): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>): MatcherResult = MatcherResult(
    value.isEmpty(),
    { "Collection should be empty but contained ${value.show().value}" },
    { "Collection should not be empty" }
  )
}

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

fun <T : Comparable<T>> beMonotonicallyIncreasing(): Matcher<List<T>> = monotonicallyIncreasing()
fun <T : Comparable<T>> monotonicallyIncreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    return testMonotonicallyIncreasingWith(value,
      Comparator { a, b -> a.compareTo(b) })
  }
}

fun <T> beMonotonicallyIncreasingWith(comparator: Comparator<in T>): Matcher<List<T>> =
  monotonicallyIncreasingWith(comparator)
fun <T> monotonicallyIncreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    return testMonotonicallyIncreasingWith(value, comparator)
  }
}
private fun<T> testMonotonicallyIncreasingWith(value: List<T>, comparator: Comparator<in T>): MatcherResult {
  val failure = value.zipWithNext().withIndex().find { (_, pair) -> comparator.compare(pair.first, pair.second) > 0 }
  val snippet = value.show().value
  val elementMessage = when (failure) {
    null -> ""
    else -> ". Element ${failure.value.second} at index ${failure.index + 1} was not monotonically increased from previous element."
  }
  return MatcherResult(
    failure == null,
    { "List [$snippet] should be monotonically increasing$elementMessage" },
    { "List [$snippet] should not be monotonically increasing" }
  )
}

fun <T : Comparable<T>> beMonotonicallyDecreasing(): Matcher<List<T>> = monotonicallyDecreasing()
fun <T : Comparable<T>> monotonicallyDecreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    return testMonotonicallyDecreasingWith(value,
      Comparator { a, b -> a.compareTo(b) })
  }
}

fun <T> beMonotonicallyDecreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = monotonicallyDecreasingWith(
  comparator)
fun <T> monotonicallyDecreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    return testMonotonicallyDecreasingWith(value, comparator)
  }
}
private fun <T> testMonotonicallyDecreasingWith(value: List<T>, comparator: Comparator<in T>): MatcherResult {
  val failure = value.zipWithNext().withIndex().find { (_, pair) -> comparator.compare(pair.first, pair.second) < 0 }
  val snippet = value.show().value
  val elementMessage = when (failure) {
    null -> ""
    else -> ". Element ${failure.value.second} at index ${failure.index + 1} was not monotonically decreased from previous element."
  }
  return MatcherResult(
    failure == null,
    { "List [$snippet] should be monotonically decreasing$elementMessage" },
    { "List [$snippet] should not be monotonically decreasing" }
  )
}

fun <T : Comparable<T>> beStrictlyIncreasing(): Matcher<List<T>> = strictlyIncreasing()
fun <T : Comparable<T>> strictlyIncreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    return testStrictlyIncreasingWith(value, Comparator { a, b -> a.compareTo(b) })
  }
}

fun <T> beStrictlyIncreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = strictlyIncreasingWith(
  comparator)
fun <T> strictlyIncreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    return testStrictlyIncreasingWith(value, comparator)
  }
}
private fun <T> testStrictlyIncreasingWith(value: List<T>, comparator: Comparator<in T>): MatcherResult {
  val failure = value.zipWithNext().withIndex().find { (_, pair) -> comparator.compare(pair.first, pair.second) >= 0 }
  val snippet = value.show().value
  val elementMessage = when (failure) {
    null -> ""
    else -> ". Element ${failure.value.second} at index ${failure.index + 1} was not strictly increased from previous element."
  }
  return MatcherResult(
    failure == null,
    { "List [$snippet] should be strictly increasing$elementMessage" },
    { "List [$snippet] should not be strictly increasing" }
  )
}

fun <T : Comparable<T>> beStrictlyDecreasing(): Matcher<List<T>> = strictlyDecreasing()
fun <T : Comparable<T>> strictlyDecreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    return testStrictlyDecreasingWith(value, Comparator { a, b -> a.compareTo(b) })
  }
}

fun <T> beStrictlyDecreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = strictlyDecreasingWith(
  comparator)
fun <T> strictlyDecreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    return testStrictlyDecreasingWith(value, comparator)
  }
}
private fun <T> testStrictlyDecreasingWith(value: List<T>, comparator: Comparator<in T>): MatcherResult {
  val failure = value.zipWithNext().withIndex().find { (_, pair) -> comparator.compare(pair.first, pair.second) <= 0 }
  val snippet = value.show().value
  val elementMessage = when (failure) {
    null -> ""
    else -> ". Element ${failure.value.second} at index ${failure.index + 1} was not strictly decreased from previous element."
  }
  return MatcherResult(
    failure == null,
    { "List [$snippet] should be strictly decreasing$elementMessage" },
    { "List [$snippet] should not be strictly decreasing" }
  )
}

