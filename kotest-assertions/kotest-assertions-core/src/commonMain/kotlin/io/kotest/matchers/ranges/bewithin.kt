package io.kotest.matchers.ranges

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Verifies that this [ClosedRange] is within another [ClosedRange].
 * This means that every element in the range is in another range as well.
 * For instance, there are two `Int` numbers in range [2, 3], and both are in range [1, 3],
 * so the range [2, 3] is within range [1, 3].
 *
 * An empty range will always fail.
 *
 * @see [shouldBeWithin]
 * @see [beWithin]
 */
infix fun <T : Comparable<T>> ClosedRange<T>.shouldBeWithin(range: ClosedRange<T>): ClosedRange<T> {
   Range.ofClosedRange(this) should beWithin(Range.ofClosedRange(range))
   return this
}

/**
 * Verifies that this [OpenEndRange] is within a [ClosedRange].
 * This means that every element in the range is in another range as well.
 * For instance, there are two `Int` numbers in [OpenEndRange] [1, 3), and both are in [ClosedRange] [1, 3],
 * so the range [1, 3) is within range [1, 2].
 *
 * An empty range will always fail.
 *
 * @see [shouldBeWithin]
 * @see [beWithin]
 */
inline infix fun <reified T : Comparable<T>> OpenEndRange<T>.shouldBeWithin(range: ClosedRange<T>): OpenEndRange<T> {
   when (T::class) {
      Int::class -> shouldBeWithinRangeOfInt(
         @Suppress("UNCHECKED_CAST") (this as OpenEndRange<Int>),
         @Suppress("UNCHECKED_CAST") (range as ClosedRange<Int>),
      )

      Long::class -> shouldBeWithinRangeOfLong(
         @Suppress("UNCHECKED_CAST") (this as OpenEndRange<Long>),
         @Suppress("UNCHECKED_CAST") (range as ClosedRange<Long>),
      )

      else -> shouldBeWithinGeneric(this, range)
   }
   return this
}

/**
 * Verifies that this [ClosedRange] is within with an [OpenEndRange].
 * This means that every element in the range is in another range as well.
 * For instance, there are two `Int` numbers in [ClosedRange] [1, 2], and both are in [OpenEndRange] [1, 3),
 * so the range [1, 2] is within range [1, 3).
 *
 * @see [shouldBeWithin]
 * @see [beWithin]
 */
infix fun <T : Comparable<T>> ClosedRange<T>.shouldBeWithin(range: OpenEndRange<T>): ClosedRange<T> {
   Range.ofClosedRange(this) should beWithin(Range.ofOpenEndRange(range))
   return this
}

/**
 * Verifies that this [OpenEndRange] is within another [OpenEndRange].
 * This means that every element in the range is in another range as well.
 * For instance, there are two `Int` numbers in [OpenEndRange] [1, 3), and both are in [OpenEndRange] [0, 3),
 * so the range [1, 3) is within range [0, 3).
 *
 * @see [shouldBeWithin]
 * @see [beWithin]
 */
infix fun <T : Comparable<T>> OpenEndRange<T>.shouldBeWithin(range: OpenEndRange<T>): OpenEndRange<T> {
   Range.ofOpenEndRange(this) should beWithin(Range.ofOpenEndRange(range))
   return this
}

/**
 * Verifies that this [ClosedRange] does not beWithin with another [ClosedRange].
 * This means that every element in the range is in another range as well.
 * For instance, there are two `Int` numbers in [ClosedRange] [2, 3], and both are in [ClosedRange] [0, 3],
 * so the range [2, 3] is within range [0, 3].
 *
 * An empty range will always fail.
 *
 * @see [shouldNotBeWithin]
 * @see [beWithin]
 */
infix fun <T : Comparable<T>> ClosedRange<T>.shouldNotBeWithin(range: ClosedRange<T>): ClosedRange<T> {
   Range.ofClosedRange(this) shouldNot beWithin(Range.ofClosedRange(range))
   return this
}

/**
 * Verifies that this [ClosedRange] is not within an [OpenEndRange].
 * This means that every element in the range is in another range.
 * For instance, there are two `Int` numbers in [ClosedRange] [2, 3], and 3 is not in [OpenEndRange] [2, 3),
 * so the range [2, 3] is not within range [2, 3).
 *
 * @see [shouldNotBeWithin]
 * @see [beWithin]
 */
infix fun <T : Comparable<T>> ClosedRange<T>.shouldNotBeWithin(range: OpenEndRange<T>): ClosedRange<T> {
   Range.ofClosedRange(this) shouldNot beWithin(Range.ofOpenEndRange(range))
   return this
}

/**
 * Verifies that this [OpenEndRange] is not within a [ClosedRange].
 * This means that every element in the range is in another range.
 * For instance, there are two `Int` numbers in [OpenEndRange] [1, 3), and 2 is not in [ClosedRange] [0, 1],
 * so the range [1, 3) is not within range [0, 1].
 *
 * An empty range will always fail.
 *
 * @see [shouldNotBeWithin]
 * @see [beWithin]
 */
inline infix fun <reified T : Comparable<T>> OpenEndRange<T>.shouldNotBeWithin(range: ClosedRange<T>): OpenEndRange<T> {

   when (T::class) {
      Int::class -> shouldNotBeWithinRangeOfInt(
         @Suppress("UNCHECKED_CAST") (this as OpenEndRange<Int>),
         @Suppress("UNCHECKED_CAST") (range as ClosedRange<Int>),
      )

      Long::class -> shouldNotBeWithinRangeOfLong(
         @Suppress("UNCHECKED_CAST") (this as OpenEndRange<Long>),
         @Suppress("UNCHECKED_CAST") (range as ClosedRange<Long>),
      )

      else -> shouldNotBeWithinGeneric(this, range)
   }
   return this
}

/**
 * Verifies that this [OpenEndRange] does not beWithin with another [OpenEndRange].
 * This means that every element in the range is in another range.
 * For instance, there are two `Int` numbers in [OpenEndRange] [1, 3), and 2 is not in [OpenEndRange] [0, 2),
 * so the range [1, 3) is not within range [0, 2).
 *
 * @see [shouldNotBeWithin]
 * @see [beWithin]
 */
infix fun <T : Comparable<T>> OpenEndRange<T>.shouldNotBeWithin(range: OpenEndRange<T>): OpenEndRange<T> {
   Range.ofOpenEndRange(this) shouldNot beWithin(Range.ofOpenEndRange(range))
   return this
}

@PublishedApi
internal fun <T : Comparable<T>> beWithin(range: Range<T>) = object : Matcher<Range<T>> {
   override fun test(value: Range<T>): MatcherResult {
      if (range.isEmpty()) throw AssertionError("Asserting content on empty range. Use Iterable.shouldBeEmpty() instead.")

      val match = range.contains(value)

      return MatcherResult(
         match,
         { "Range ${value.print().value} should be within ${range.print().value}, but it isn't" },
         { "Range ${value.print().value} should not be within ${range.print().value}, but it is" }
      )
   }
}

@PublishedApi
internal fun shouldBeWithinRangeOfInt(
   value: OpenEndRange<Int>,
   range: ClosedRange<Int>
) {
   value should beWithinRangeOfInt(range)
}

@PublishedApi
internal fun shouldNotBeWithinRangeOfInt(
   value: OpenEndRange<Int>,
   range: ClosedRange<Int>
) {
   value shouldNot beWithinRangeOfInt(range)
}

@PublishedApi
internal fun beWithinRangeOfInt(
   range: ClosedRange<Int>
) = object : Matcher<OpenEndRange<Int>> {
   override fun test(value: OpenEndRange<Int>): MatcherResult {
      return resultForWithin(range, value, (range.endInclusive + 1))
   }
}

@PublishedApi
internal fun shouldBeWithinRangeOfLong(
   value: OpenEndRange<Long>,
   range: ClosedRange<Long>
) {
   value should beWithinRangeOfLong(range)
}

@PublishedApi
internal fun shouldNotBeWithinRangeOfLong(
   value: OpenEndRange<Long>,
   range: ClosedRange<Long>
) {
   value shouldNot beWithinRangeOfLong(range)
}

@PublishedApi
internal fun beWithinRangeOfLong(
   range: ClosedRange<Long>
) = object : Matcher<OpenEndRange<Long>> {
   override fun test(value: OpenEndRange<Long>): MatcherResult {
      return resultForWithin(range, value, (range.endInclusive + 1L))
   }
}

@PublishedApi
internal fun <T : Comparable<T>> shouldBeWithinGeneric(
   value: OpenEndRange<T>,
   range: ClosedRange<T>
) {
   Range.ofOpenEndRange(value) should beWithin(Range.ofClosedRange(range))
}

@PublishedApi
internal fun <T : Comparable<T>> shouldNotBeWithinGeneric(
   value: OpenEndRange<T>,
   range: ClosedRange<T>
) {
   Range.ofOpenEndRange(value) shouldNot beWithin(Range.ofClosedRange(range))
}

@PublishedApi
internal fun <T : Comparable<T>> resultForWithin(
   range: ClosedRange<T>,
   value: OpenEndRange<T>,
   valueAfterRangeEnd: T
): MatcherResult {
   if (range.isEmpty()) throw AssertionError("Asserting content on empty range. Use Iterable.shouldBeEmpty() instead.")
   val match = (range.start <= value.start) && (value.endExclusive <= valueAfterRangeEnd)
   val valueStr = "[${value.start}, ${value.endExclusive})"
   val rangeStr = "[${range.start}, ${range.endInclusive}]"
   return MatcherResult(
      match,
      { "Range $valueStr should be within $rangeStr, but it isn't" },
      { "Range $valueStr should not be within $rangeStr, but it is" }
   )
}
