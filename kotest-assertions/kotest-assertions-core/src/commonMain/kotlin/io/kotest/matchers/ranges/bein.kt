package io.kotest.matchers.ranges

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Verifies that this element is in [ClosedRange] by comparing value
 *
 * Assertion to check that this element is in [ClosedRange]. This assertion checks by value, and not by reference,
 * therefore even if the exact instance is not in [ClosedRange] but another instance with same value is present, the
 * test will pass.
 *
 * An empty range will always fail. If you need to check for empty range, use [ClosedRange.shouldBeEmpty]
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
infix fun <T: Comparable<T>> T.shouldBeIn(range: ClosedRange<T>): T {
   this should beIn(range)
   return this
}

/**
 * Verifies that this element is NOT any of [range]
 *
 * Assertion to check that this element is not any of [range]. This assertion checks by value, and not by reference,
 * therefore any instance with same value must not be in [range], or this will fail.
 *
 * An empty range will always fail. If you need to check for empty range, use [Iterable.shouldBeEmpty]
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
infix fun <T: Comparable<T>> T.shouldNotBeIn(range: ClosedRange<T>): T {
   this shouldNot beIn(range)
   return this
}

/**
 *  Matcher that verifies that this element is in [range] by comparing value
 *
 * Assertion to check that this element is in [range]. This assertion checks by value, and not by reference,
 * therefore even if the exact instance is not in [range] but another instance with same value is present, the
 * test will pass.
 *
 * An empty range will always fail. If you need to check for empty range, use [Iterable.shouldBeEmpty]
 *
 */
fun <T: Comparable<T>> beIn(range: ClosedRange<T>) = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      if (range.isEmpty()) throw AssertionError("Asserting content on empty range. Use Iterable.shouldBeEmpty() instead.")

      val match = value in range

      return MatcherResult(
         match,
         { "Range should contain ${value.print().value}, but doesn't. Possible values: ${range.print().value}" },
         { "Range should not contain ${value.print().value}, but does. Forbidden values: ${range.print().value}" }
     )
   }
}
