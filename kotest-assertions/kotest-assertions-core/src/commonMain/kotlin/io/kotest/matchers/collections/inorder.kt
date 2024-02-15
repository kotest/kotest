package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/** Assert that a collection contains a given subsequence, possibly with values in between. */
fun <T> containsInOrder(vararg ts: T): Matcher<Collection<T>?> = containsInOrder(ts.asList())

/** Assert that a collection contains a given subsequence, possibly with values in between. */
fun <T> containsInOrder(subsequence: List<T>): Matcher<Collection<T>?> = neverNullMatcher { actual ->
   require(subsequence.isNotEmpty()) { "expected values must not be empty" }

   var subsequenceIndex = 0
   val actualIterator = actual.iterator()

   while (actualIterator.hasNext() && subsequenceIndex < subsequence.size) {
      if (actualIterator.next() == subsequence[subsequenceIndex]) subsequenceIndex += 1
   }

   val mismatchDescription = if(subsequenceIndex == subsequence.size) "" else
      ", could not match element ${subsequence.elementAt(subsequenceIndex).print().value} at index $subsequenceIndex"

   MatcherResult(
      subsequenceIndex == subsequence.size,
      { "${actual.print().value} did not contain the elements ${subsequence.print().value} in order$mismatchDescription" },
      { "${actual.print().value} should not contain the elements ${subsequence.print().value} in order" }
   )
}

fun <T> Iterable<T>.shouldContainInOrder(vararg ts: T) = toList().shouldContainInOrder(*ts)
fun <T> Array<T>.shouldContainInOrder(vararg ts: T) = asList().shouldContainInOrder(*ts)
fun <T> List<T>.shouldContainInOrder(vararg ts: T) = this.shouldContainInOrder(ts.toList())
infix fun <T> Iterable<T>.shouldContainInOrder(expected: List<T>) = toList().shouldContainInOrder(expected)
infix fun <T> Array<T>.shouldContainInOrder(expected: List<T>) = asList().shouldContainInOrder(expected)
infix fun <T> List<T>.shouldContainInOrder(expected: List<T>) = this should containsInOrder(expected)
infix fun <T> Iterable<T>.shouldNotContainInOrder(expected: Iterable<T>) = toList().shouldNotContainInOrder(expected.toList())
infix fun <T> Array<T>.shouldNotContainInOrder(expected: Array<T>) = asList().shouldNotContainInOrder(expected.asList())
infix fun <T> Iterable<T>.shouldNotContainInOrder(expected: List<T>) = toList().shouldNotContainInOrder(expected)
infix fun <T> Array<T>.shouldNotContainInOrder(expected: List<T>) = asList().shouldNotContainInOrder(expected)
infix fun <T> List<T>.shouldNotContainInOrder(expected: List<T>) = this shouldNot containsInOrder(expected)
