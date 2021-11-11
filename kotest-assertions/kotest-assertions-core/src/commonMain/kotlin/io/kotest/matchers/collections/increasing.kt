package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot


fun <T : Comparable<T>> Iterable<T>.shouldBeStrictlyIncreasing(): Iterable<T> {
   toList().shouldBeStrictlyIncreasing()
   return this
}

fun <T : Comparable<T>> Array<T>.shouldBeStrictlyIncreasing(): Array<T> {
   asList().shouldBeStrictlyIncreasing()
   return this
}

fun <T : Comparable<T>> List<T>.shouldBeStrictlyIncreasing(): List<T> {
   this should beStrictlyIncreasing()
   return this
}

fun <T : Comparable<T>> Iterable<T>.shouldNotBeStrictlyIncreasing(): Iterable<T> {
   toList().shouldNotBeStrictlyIncreasing()
   return this
}

fun <T : Comparable<T>> Array<T>.shouldNotBeStrictlyIncreasing(): Array<T> {
   asList().shouldNotBeStrictlyIncreasing()
   return this
}

fun <T : Comparable<T>> List<T>.shouldNotBeStrictlyIncreasing(): List<T> {
   this shouldNot beStrictlyIncreasing()
   return this
}



fun <T : Comparable<T>> Iterable<T>.shouldBeMonotonicallyIncreasing(): Iterable<T> {
   toList().shouldBeMonotonicallyIncreasing()
   return this
}

fun <T : Comparable<T>> Array<T>.shouldBeMonotonicallyIncreasing(): Array<T> {
   asList().shouldBeMonotonicallyIncreasing()
   return this
}

fun <T : Comparable<T>> List<T>.shouldBeMonotonicallyIncreasing(): List<T> {
   this should beMonotonicallyIncreasing()
   return this
}

fun <T : Comparable<T>> Iterable<T>.shouldNotBeMonotonicallyIncreasing(): Iterable<T> {
   toList().shouldNotBeMonotonicallyIncreasing()
   return this
}

fun <T : Comparable<T>> Array<T>.shouldNotBeMonotonicallyIncreasing(): Array<T> {
   asList().shouldNotBeMonotonicallyIncreasing()
   return this
}

fun <T : Comparable<T>> List<T>.shouldNotBeMonotonicallyIncreasing(): List<T> {
   this shouldNot beMonotonicallyIncreasing()
   return this
}

fun <T> List<T>.shouldBeMonotonicallyIncreasingWith(comparator: Comparator<in T>): List<T> {
   this should beMonotonicallyIncreasingWith(comparator)
   return this
}

fun <T> Iterable<T>.shouldBeMonotonicallyIncreasingWith(comparator: Comparator<in T>): Iterable<T> {
   toList().shouldBeMonotonicallyIncreasingWith(comparator)
   return this
}

fun <T> Array<T>.shouldBeMonotonicallyIncreasingWith(comparator: Comparator<in T>): Array<T> {
   asList().shouldBeMonotonicallyIncreasingWith(comparator)
   return this
}

fun <T> List<T>.shouldNotBeMonotonicallyIncreasingWith(comparator: Comparator<in T>): List<T> {
   this shouldNot beMonotonicallyIncreasingWith(comparator)
   return this
}

fun <T> Iterable<T>.shouldNotBeMonotonicallyIncreasingWith(comparator: Comparator<in T>): Iterable<T> {
   toList().shouldNotBeMonotonicallyIncreasingWith(comparator)
   return this
}


fun <T> Array<T>.shouldNotBeMonotonicallyIncreasingWith(comparator: Comparator<in T>): Array<T> {
   asList().shouldNotBeMonotonicallyIncreasingWith(comparator)
   return this
}


fun <T> List<T>.shouldBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   this should beStrictlyIncreasingWith(comparator)

fun <T> Iterable<T>.shouldBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   toList().shouldBeStrictlyIncreasingWith(comparator)

fun <T> Array<T>.shouldBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   asList().shouldBeStrictlyIncreasingWith(comparator)

fun <T> List<T>.shouldNotBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   this shouldNot beStrictlyIncreasingWith(comparator)

fun <T> Iterable<T>.shouldNotBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   toList().shouldNotBeStrictlyIncreasingWith(comparator)

fun <T> Array<T>.shouldNotBeStrictlyIncreasingWith(comparator: Comparator<in T>) =
   asList().shouldNotBeStrictlyIncreasingWith(comparator)


fun <T : Comparable<T>> beStrictlyIncreasing(): Matcher<List<T>> = strictlyIncreasing()
fun <T : Comparable<T>> strictlyIncreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      return testStrictlyIncreasingWith(value) { a, b -> a.compareTo(b) }
   }
}

fun <T> beStrictlyIncreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = strictlyIncreasingWith(comparator)
fun <T> strictlyIncreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      return testStrictlyIncreasingWith(value, comparator)
   }
}

private fun <T> testStrictlyIncreasingWith(value: List<T>, comparator: Comparator<in T>): MatcherResult {
   val failure = value.zipWithNext().withIndex().find { (_, pair) -> comparator.compare(pair.first, pair.second) >= 0 }
   val snippet = value.print().value
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


fun <T : Comparable<T>> beMonotonicallyIncreasing(): Matcher<List<T>> = monotonicallyIncreasing()
fun <T : Comparable<T>> monotonicallyIncreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      return testMonotonicallyIncreasingWith(value) { a, b -> a.compareTo(b) }
   }
}

fun <T> beMonotonicallyIncreasingWith(comparator: Comparator<in T>): Matcher<List<T>> =
   monotonicallyIncreasingWith(comparator)

fun <T> monotonicallyIncreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      return testMonotonicallyIncreasingWith(value, comparator)
   }
}

private fun <T> testMonotonicallyIncreasingWith(value: List<T>, comparator: Comparator<in T>): MatcherResult {
   val failure = value.zipWithNext().withIndex().find { (_, pair) -> comparator.compare(pair.first, pair.second) > 0 }
   val snippet = value.print().value
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
