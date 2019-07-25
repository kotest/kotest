package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.MatcherResult
import io.kotlintest.neverNullMatcher
import io.kotlintest.stringRepr

fun <T> haveSizeMatcher(size: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
    MatcherResult(
      value.size == size,
      { "Collection should have size $size but has size ${value.size}" },
      { "Collection should not have size $size" }
    )
}


fun <T> beEmpty(): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>): MatcherResult = MatcherResult(
    value.isEmpty(),
    { "Collection should be empty but contained ${stringRepr(value)}" },
    { "Collection should not be empty" }
  )
}


fun <T> containAll(vararg ts: T) = containAll(ts.asList())
fun <T> containAll(ts: Collection<T>): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = MatcherResult(
    ts.all { value.contains(it) },
    { "Collection should contain all of ${ts.joinToString(", ", limit = 10) { stringRepr(it) }}" },
    { "Collection should not contain all of ${ts.joinToString(", ", limit = 10) { stringRepr(it) }}" }
  )
}

fun <T> containsInOrder(vararg ts: T): Matcher<Collection<T>?> = containsInOrder(ts.asList())
/** Assert that a collection contains a given subsequence, possibly with values in between. */
fun <T> containsInOrder(subsequence: List<T>): Matcher<Collection<T>?> = neverNullMatcher { actual ->
  assert(subsequence.isNotEmpty()) { "expected values must not be empty" }

  var subsequenceIndex = 0
  val actualIterator = actual.iterator()

  while (actualIterator.hasNext() && subsequenceIndex < subsequence.size) {
    if (actualIterator.next() == subsequence[subsequenceIndex]) subsequenceIndex += 1
  }

  MatcherResult(
    subsequenceIndex == subsequence.size,
    { "${stringRepr(actual)} did not contain the elements ${stringRepr(subsequence)} in order" },
    { "${stringRepr(actual)} should not contain the elements ${stringRepr(subsequence)} in order" }
  )
}

fun <T> haveSize(size: Int): Matcher<Collection<T>> = haveSizeMatcher(size)

fun <T> singleElement(t: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = MatcherResult(
    value.size == 1 && value.first() == t,
    { "Collection should be a single element of $t but has ${value.size} elements" },
    { "Collection should not be a single element of $t" }
  )
}

fun <T : Comparable<T>> beSorted(): Matcher<List<T>> = sorted()
fun <T : Comparable<T>> sorted(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    val failure = value.withIndex().firstOrNull { (i, it) -> i != value.lastIndex && it > value[i + 1] }
    val snippet = value.joinToString(",", limit = 10)
    val elementMessage = when (failure) {
      null -> ""
      else -> ". Element ${failure.value} at index ${failure.index} was greater than element ${value[failure.index + 1]}"
    }
    return MatcherResult(
      failure == null,
      { "List [$snippet] should be sorted$elementMessage" },
      { "List [$snippet] should not be sorted" }
    )
  }
}

fun <T : Comparable<T>> beMonotonicallyIncreasing(): Matcher<List<T>> = monotonicallyIncreasing()
fun <T : Comparable<T>> monotonicallyIncreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    val failure = value.zipWithNext().withIndex().find { (_, pair) -> pair.first > pair.second }
    val snippet = value.joinToString(",", limit = 10)
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
}

fun <T : Comparable<T>> beMonotonicallyDecreasing(): Matcher<List<T>> = monotonicallyDecreasing()
fun <T : Comparable<T>> monotonicallyDecreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    val failure = value.zipWithNext().withIndex().find { (_, pair) -> pair.first < pair.second }
    val snippet = value.joinToString(",", limit = 10)
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
}

fun <T : Comparable<T>> beStrictlyIncreasing(): Matcher<List<T>> = strictlyIncreasing()
fun <T : Comparable<T>> strictlyIncreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    val failure = value.zipWithNext().withIndex().find { (_, pair) -> pair.first >= pair.second }
    val snippet = value.joinToString(",", limit = 10)
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
}

fun <T : Comparable<T>> beStrictlyDecreasing(): Matcher<List<T>> = strictlyDecreasing()
fun <T : Comparable<T>> strictlyDecreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): MatcherResult {
    val failure = value.zipWithNext().withIndex().find { (_, pair) -> pair.first <= pair.second }
    val snippet = value.joinToString(",", limit = 10)
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
}

