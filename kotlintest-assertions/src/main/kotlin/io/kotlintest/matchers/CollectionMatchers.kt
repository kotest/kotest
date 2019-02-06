package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.neverNullMatcher
import io.kotlintest.stringRepr

fun <T> haveSizeMatcher(size: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
      Result(
          value.size == size,
          "Collection should have size $size but has size ${value.size}",
          "Collection should not have size $size"
      )
}


fun <T> beEmpty(): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>): Result = Result(
      value.isEmpty(),
      "Collection should be empty but contained ${stringRepr(value)}",
      "Collection should not be empty"
  )
}

@Deprecated("use containAll", ReplaceWith("containsAll(ts.asList())"))
fun <T> containsAll(vararg ts: T) = containAll(ts.asList())

@Deprecated("use containAll", ReplaceWith("containAll(ts)"))
fun <T> containsAll(ts: List<T>): Matcher<Collection<T>> = containAll(ts)


fun <T> containAll(vararg ts: T) = containAll(ts.asList())
fun <T> containAll(ts: Collection<T>): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>): Result {
    val joinedString by lazy { ts.joinToString(", ", limit = 10) { stringRepr(it) } }
    val failureMessage by lazy { "Collection should contain all of $joinedString" }
    val negatedFailureMessage by lazy { "Collection should not contain all of $joinedString" }
    return Result(ts.all { value.contains(it) }, failureMessage, negatedFailureMessage)
  }
}

fun <T> containsInOrder(vararg ts: T): Matcher<Collection<T>?> = containsInOrder(ts.asList())
/** Assert that a collection contains a given subsequence, possibly with values in between. */
fun <T> containsInOrder(subsequence: List<T>): Matcher<Collection<T>?> = neverNullMatcher { actual ->
  assert(subsequence.isNotEmpty(), { "expected values must not be empty" })

  var subsequenceIndex = 0
  val actualIterator = actual.iterator()

  while (actualIterator.hasNext() && subsequenceIndex < subsequence.size) {
    if (actualIterator.next() == subsequence[subsequenceIndex]) subsequenceIndex += 1
  }

  Result(
      subsequenceIndex == subsequence.size,
      "${stringRepr(actual)} did not contain the elements ${stringRepr(subsequence)} in order",
      "${stringRepr(actual)} should not contain the elements ${stringRepr(subsequence)} in order"
  )
}

fun <T> haveSize(size: Int): Matcher<Collection<T>> = haveSizeMatcher(size)

@Deprecated("use io.kotlintest.matchers.collection.contain(t)", ReplaceWith("containsMatcher(t)"))
fun <T> contain(t: T): Matcher<Collection<T>> = io.kotlintest.matchers.collections.contain(t)

fun <T> singleElement(t: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(
      value.size == 1 && value.first() == t,
      "Collection should be a single element of $t but has ${value.size} elements",
      "Collection should not be a single element of $t"
  )
}

fun <T : Comparable<T>> beSorted(): Matcher<List<T>> = sorted()
fun <T : Comparable<T>> sorted(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>): Result {
    val failure = value.withIndex().find { (i, it) -> i != value.lastIndex && it > value[i+1] }
    val elementMessage by lazy {
      when (failure) {
        null -> ""
        else -> ". Element ${failure.value} at index ${failure.index} was greater than element ${value[failure.index+1]}"
      }
    }
    val snippet by lazy { value.joinToString(",", limit = 10) }
    val failureMessage by lazy { "List [$snippet] should be sorted$elementMessage" }
    val negatedFailureMessage by lazy { "List [$snippet] should not be sorted" }
    return Result(failure == null, failureMessage, negatedFailureMessage)
  }
}
