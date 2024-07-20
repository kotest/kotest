package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T : Comparable<T>, I : Iterable<T>> I.shouldBeMonotonicallyDecreasing(): I {
   toList().shouldBeMonotonicallyDecreasing()
   return this
}

fun <T : Comparable<T>> Array<T>.shouldBeMonotonicallyDecreasing(): Array<T> {
   asList().shouldBeMonotonicallyDecreasing()
   return this
}

fun <T : Comparable<T>, S : Sequence<T>> S.shouldBeMonotonicallyDecreasing(): S {
   asIterable().shouldBeMonotonicallyDecreasing()
   return this
}

fun <T : Comparable<T>> List<T>.shouldBeMonotonicallyDecreasing(): List<T> {
   this should beMonotonicallyDecreasing()
   return this
}

fun <T : Comparable<T>, I : Iterable<T>> I.shouldNotBeMonotonicallyDecreasing(): I {
   toList().shouldNotBeMonotonicallyDecreasing()
   return this
}

fun <T : Comparable<T>> Array<T>.shouldNotBeMonotonicallyDecreasing(): Array<T> {
   asList().shouldNotBeMonotonicallyDecreasing()
   return this
}

fun <T : Comparable<T>, S : Sequence<T>> S.shouldNotBeMonotonicallyDecreasing(): S {
   asIterable().shouldNotBeMonotonicallyDecreasing()
   return this
}

fun <T : Comparable<T>> List<T>.shouldNotBeMonotonicallyDecreasing(): List<T> {
   this shouldNot beMonotonicallyDecreasing<T>()
   return this
}

infix fun <T> List<T>.shouldBeMonotonicallyDecreasingWith(comparator: Comparator<in T>): List<T> {
   this should beMonotonicallyDecreasingWith(comparator)
   return this
}

infix fun <T, I : Iterable<T>> I.shouldBeMonotonicallyDecreasingWith(comparator: Comparator<in T>): I {
   toList().shouldBeMonotonicallyDecreasingWith(comparator)
   return this
}

infix fun <T> Array<T>.shouldBeMonotonicallyDecreasingWith(comparator: Comparator<in T>): Array<T> {
   asList().shouldBeMonotonicallyDecreasingWith(comparator)
   return this
}

infix fun <T> Sequence<T>.shouldBeMonotonicallyDecreasingWith(comparator: Comparator<in T>): Sequence<T> {
   asIterable().shouldBeMonotonicallyDecreasingWith(comparator)
   return this
}

infix fun <T> List<T>.shouldNotBeMonotonicallyDecreasingWith(comparator: Comparator<in T>): List<T> {
   this shouldNot beMonotonicallyDecreasingWith(comparator)
   return this
}

infix fun <T, I : Iterable<T>> I.shouldNotBeMonotonicallyDecreasingWith(comparator: Comparator<in T>): I {
   toList().shouldNotBeMonotonicallyDecreasingWith(comparator)
   return this
}

infix fun <T> Array<T>.shouldNotBeMonotonicallyDecreasingWith(comparator: Comparator<in T>): Array<T> {
   asList().shouldNotBeMonotonicallyDecreasingWith(comparator)
   return this
}

infix fun <T> Sequence<T>.shouldNotBeMonotonicallyDecreasingWith(comparator: Comparator<in T>): Sequence<T> {
   asIterable().shouldNotBeMonotonicallyDecreasingWith(comparator)
   return this
}

fun <T : Comparable<T>> Iterable<T>.shouldBeStrictlyDecreasing() = toList().shouldBeStrictlyDecreasing()
fun <T : Comparable<T>> List<T>.shouldBeStrictlyDecreasing() = this should beStrictlyDecreasing()
fun <T : Comparable<T>> Array<T>.shouldBeStrictlyDecreasing() = toList().shouldBeStrictlyDecreasing()
fun <T : Comparable<T>> Sequence<T>.shouldBeStrictlyDecreasing() = toList().shouldBeStrictlyDecreasing()

fun <T : Comparable<T>> Iterable<T>.shouldNotBeStrictlyDecreasing() = toList().shouldNotBeStrictlyDecreasing()
fun <T : Comparable<T>> List<T>.shouldNotBeStrictlyDecreasing() = this shouldNot beStrictlyDecreasing()
fun <T : Comparable<T>> Sequence<T>.shouldNotBeStrictlyDecreasing() = this.asIterable().shouldNotBeStrictlyDecreasing()
fun <T : Comparable<T>> Array<T>.shouldNotBeStrictlyDecreasing() = toList().shouldNotBeStrictlyDecreasing()

infix fun <T> List<T>.shouldBeStrictlyDecreasingWith(comparator: Comparator<in T>): List<T> {
   this should beStrictlyDecreasingWith(comparator)
   return this
}

infix fun <T, I : Iterable<T>> I.shouldBeStrictlyDecreasingWith(comparator: Comparator<in T>): I {
   toList().shouldBeStrictlyDecreasingWith(comparator)
   return this
}

infix fun <T> Array<T>.shouldBeStrictlyDecreasingWith(comparator: Comparator<in T>): Array<T> {
   asList().shouldBeStrictlyDecreasingWith(comparator)
   return this
}

infix fun <T> Sequence<T>.shouldBeStrictlyDecreasingWith(comparator: Comparator<in T>): Sequence<T> {
   asIterable().shouldBeStrictlyDecreasingWith(comparator)
   return this
}

infix fun <T> List<T>.shouldNotBeStrictlyDecreasingWith(comparator: Comparator<in T>): List<T> {
   this shouldNot beStrictlyDecreasingWith(comparator)
   return this
}

infix fun <T, I : Iterable<T>> I.shouldNotBeStrictlyDecreasingWith(comparator: Comparator<in T>): I {
   toList().shouldNotBeStrictlyDecreasingWith(comparator)
   return this
}

infix fun <T> Array<T>.shouldNotBeStrictlyDecreasingWith(comparator: Comparator<in T>): Array<T> {
   asList().shouldNotBeStrictlyDecreasingWith(comparator)
   return this
}

infix fun <T> Sequence<T>.shouldNotBeStrictlyDecreasingWith(comparator: Comparator<in T>): Sequence<T> {
   asIterable().shouldNotBeStrictlyDecreasingWith(comparator)
   return this
}

fun <T : Comparable<T>> beMonotonicallyDecreasing(): Matcher<List<T>> = monotonicallyDecreasing()
fun <T : Comparable<T>> monotonicallyDecreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      return testMonotonicallyDecreasingWith(value) { a, b -> a.compareTo(b) }
   }
}

fun <T> beMonotonicallyDecreasingWith(comparator: Comparator<in T>): Matcher<List<T>> =
   monotonicallyDecreasingWith(comparator)

fun <T> monotonicallyDecreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      return testMonotonicallyDecreasingWith(value, comparator)
   }
}

private fun <T> testMonotonicallyDecreasingWith(value: List<T>, comparator: Comparator<in T>): MatcherResult {
   val failure = value.zipWithNext().withIndex().find { (_, pair) -> comparator.compare(pair.first, pair.second) < 0 }
   val snippet = value.print().value
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


fun <T : Comparable<T>> beStrictlyDecreasing(): Matcher<List<T>> = strictlyDecreasing()
fun <T : Comparable<T>> strictlyDecreasing(): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      return testStrictlyDecreasingWith(value) { a, b -> a.compareTo(b) }
   }
}

fun <T> beStrictlyDecreasingWith(comparator: Comparator<in T>): Matcher<List<T>> =
   strictlyDecreasingWith(comparator)

fun <T> strictlyDecreasingWith(comparator: Comparator<in T>): Matcher<List<T>> = object : Matcher<List<T>> {
   override fun test(value: List<T>): MatcherResult {
      return testStrictlyDecreasingWith(value, comparator)
   }
}

private fun <T> testStrictlyDecreasingWith(value: List<T>, comparator: Comparator<in T>): MatcherResult {
   val failure = value.zipWithNext().withIndex().find { (_, pair) -> comparator.compare(pair.first, pair.second) <= 0 }
   val snippet = value.print().value
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

