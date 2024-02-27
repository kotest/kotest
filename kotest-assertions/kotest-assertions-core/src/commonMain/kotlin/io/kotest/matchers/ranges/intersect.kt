package io.kotest.matchers.ranges

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Verifies that this [ClosedRange] intersects with another [ClosedRange].
 *
 * Assertion to check that this [ClosedRange] intersects with another [ClosedRange].
 *
 * An empty range will always fail. If you need to check for empty range, use [ClosedRange.shouldBeEmpty]
 *
 * @see [shouldIntersect]
 * @see [intersect]
 */
infix fun <T: Comparable<T>> ClosedRange<T>.shouldIntersect(range: ClosedRange<T>): ClosedRange<T> {
   Range.ofClosedRange(this) should intersect(Range.ofClosedRange(range))
   return this
}

/**
 * Verifies that this [OpenEndRange] intersects with a [ClosedRange].
 *
 * Assertion to check that this [OpenEndRange] intersects with a [ClosedRange].
 *
 * An empty range will always fail. If you need to check for empty range, use [ClosedRange.shouldBeEmpty]
 *
 * @see [shouldIntersect]
 * @see [intersect]
 */
@OptIn(ExperimentalStdlibApi::class)
infix fun <T: Comparable<T>> OpenEndRange<T>.shouldIntersect(range: ClosedRange<T>): OpenEndRange<T> {
   Range.ofOpenEndRange(this) should intersect(Range.ofClosedRange(range))
   return this
}

/**
 * Verifies that this [ClosedRange] intersects with an [OpenEndRange].
 *
 * Assertion to check that this [ClosedRange] intersects with an [OpenEndRange].
 *
 * @see [shouldIntersect]
 * @see [intersect]
 */
@OptIn(ExperimentalStdlibApi::class)
infix fun <T: Comparable<T>> ClosedRange<T>.shouldIntersect(range: OpenEndRange<T>): ClosedRange<T> {
   Range.ofClosedRange(this) should intersect(Range.ofOpenEndRange(range))
   return this
}

/**
 * Verifies that this [OpenEndRange] intersects with another [OpenEndRange].
 *
 * Assertion to check that this [OpenEndRange] intersects with another [OpenEndRange].
 *
 * @see [shouldIntersect]
 * @see [intersect]
 */
@OptIn(ExperimentalStdlibApi::class)
infix fun <T: Comparable<T>> OpenEndRange<T>.shouldIntersect(range: OpenEndRange<T>): OpenEndRange<T> {
   Range.ofOpenEndRange(this) should intersect(Range.ofOpenEndRange(range))
   return this
}

/**
 * Verifies that this [ClosedRange] does not intersect with another [ClosedRange].
 *
 * Assertion to check that this [ClosedRange] does not intersect with another [ClosedRange].
 *
 * An empty range will always fail. If you need to check for empty range, use [Iterable.shouldBeEmpty]
 *
 * @see [shouldNotIntersect]
 * @see [intersect]
 */
infix fun <T: Comparable<T>> ClosedRange<T>.shouldNotIntersect(range: ClosedRange<T>): ClosedRange<T> {
   Range.ofClosedRange(this) shouldNot intersect(Range.ofClosedRange(range))
   return this
}

/**
 * Verifies that this [ClosedRange] does not intersect with an [OpenEndRange].
 *
 * Assertion to check that this [ClosedRange] does not intersect with an [OpenEndRange].
 *
 * @see [shouldNotIntersect]
 * @see [intersect]
 */
@OptIn(ExperimentalStdlibApi::class)
infix fun <T: Comparable<T>> ClosedRange<T>.shouldNotIntersect(range: OpenEndRange<T>): ClosedRange<T> {
   Range.ofClosedRange(this) shouldNot intersect(Range.ofOpenEndRange(range))
   return this
}

/**
 * Verifies that this [OpenEndRange] does not intersect with a [ClosedRange].
 *
 * Assertion to check that this [OpenEndRange] does not intersect with a [ClosedRange].
 *
 * An empty range will always fail. If you need to check for empty range, use [ClosedRange.shouldBeEmpty]
 *
 * @see [shouldNotIntersect]
 * @see [intersect]
 */
@OptIn(ExperimentalStdlibApi::class)
infix fun <T: Comparable<T>> OpenEndRange<T>.shouldNotIntersect(range: ClosedRange<T>): OpenEndRange<T> {
   Range.ofOpenEndRange(this) shouldNot intersect(Range.ofClosedRange(range))
   return this
}

/**
 * Verifies that this [OpenEndRange] does not intersect with another [OpenEndRange].
 *
 * Assertion to check that this [OpenEndRange] does not intersect with another [OpenEndRange].
 *
 * @see [shouldNotIntersect]
 * @see [intersect]
 */
@OptIn(ExperimentalStdlibApi::class)
infix fun <T: Comparable<T>> OpenEndRange<T>.shouldNotIntersect(range: OpenEndRange<T>): OpenEndRange<T> {
   Range.ofOpenEndRange(this) shouldNot intersect(Range.ofOpenEndRange(range))
   return this
}

/**
 *  Matcher that verifies that this [range] intersects with another [range]
 *
 * Assertion to check that this [range] intersects with another [range].
 *
 * An empty range will always fail. If you need to check for empty range, use [Iterable.shouldBeEmpty]
 *
 */
fun <T: Comparable<T>> intersect(range: Range<T>) = object : Matcher<Range<T>> {
   override fun test(value: Range<T>): MatcherResult {
      if (range.isEmpty()) throw AssertionError("Asserting content on empty range. Use Iterable.shouldBeEmpty() instead.")

      val match = range.intersect(value)

      return MatcherResult(
         match,
         { "Range ${value.print().value} should intersect ${range.print().value}, but doesn't" },
         { "Range ${value.print().value} should not intersect ${range.print().value}, but does" }
      )
   }
}

