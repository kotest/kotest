package io.kotest.matchers.ranges

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Verifies that this [ClosedRange] beWithin with another [ClosedRange].
 *
 * Assertion to check that this [ClosedRange] beWithin with another [ClosedRange].
 *
 * An empty range will always fail. If you need to check for empty range, use [ClosedRange.shouldBeEmpty]
 *
 * @see [shouldBeWithin]
 * @see [beWithin]
 */
infix fun <T: Comparable<T>> ClosedRange<T>.shouldBeWithin(range: ClosedRange<T>): ClosedRange<T> {
   Range.of(this) should beWithin(Range.of(range))
   return this
}

/**
 * Verifies that this [OpenEndRange] beWithins with a [ClosedRange].
 *
 * Assertion to check that this [OpenEndRange] beWithins with a [ClosedRange].
 *
 * An empty range will always fail. If you need to check for empty range, use [ClosedRange.shouldBeEmpty]
 *
 * @see [shouldbeWithin]
 * @see [beWithin]
 */
@OptIn(ExperimentalStdlibApi::class)
@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
inline infix fun <reified T: Comparable<T>> OpenEndRange<T>.shouldBeWithin(range: ClosedRange<T>): OpenEndRange<T> {
   when(T::class) {
      Int::class -> shouldBeWithinRangeOfInt(this as OpenEndRange<Int>, range as ClosedRange<Int>)
      Long::class -> shouldBeWithinRangeOfLong(this as OpenEndRange<Long>, range as ClosedRange<Long>)
      else -> Range.of(this) should beWithin(Range.of(range))
   }
   return this
}

/**
 * Verifies that this [ClosedRange] beWithins with an [OpenEndRange].
 *
 * Assertion to check that this [ClosedRange] beWithins with an [OpenEndRange].
 *
 * @see [shouldbeWithin]
 * @see [beWithin]
 */
@OptIn(ExperimentalStdlibApi::class)
infix fun <T: Comparable<T>> ClosedRange<T>.shouldBeWithin(range: OpenEndRange<T>): ClosedRange<T> {
   Range.of(this) should beWithin(Range.of(range))
   return this
}

/**
 * Verifies that this [OpenEndRange] beWithins with another [OpenEndRange].
 *
 * Assertion to check that this [OpenEndRange] beWithins with another [OpenEndRange].
 *
 * @see [shouldbeWithin]
 * @see [beWithin]
 */
@OptIn(ExperimentalStdlibApi::class)
infix fun <T: Comparable<T>> OpenEndRange<T>.shouldBeWithin(range: OpenEndRange<T>): OpenEndRange<T> {
   Range.of(this) should beWithin(Range.of(range))
   return this
}

/**
 * Verifies that this [ClosedRange] does not beWithin with another [ClosedRange].
 *
 * Assertion to check that this [ClosedRange] does not beWithin with another [ClosedRange].
 *
 * An empty range will always fail. If you need to check for empty range, use [Iterable.shouldBeEmpty]
 *
 * @see [shouldNotbeWithin]
 * @see [beWithin]
 */
infix fun <T: Comparable<T>> ClosedRange<T>.shouldNotBeWithin(range: ClosedRange<T>): ClosedRange<T> {
   Range.of(this) shouldNot beWithin(Range.of(range))
   return this
}

/**
 * Verifies that this [ClosedRange] does not beWithin with an [OpenEndRange].
 *
 * Assertion to check that this [ClosedRange] does not beWithin with an [OpenEndRange].
 *
 * @see [shouldNotbeWithin]
 * @see [beWithin]
 */
@OptIn(ExperimentalStdlibApi::class)
infix fun <T: Comparable<T>> ClosedRange<T>.shouldNotBeWithin(range: OpenEndRange<T>): ClosedRange<T> {
   Range.of(this) shouldNot beWithin(Range.of(range))
   return this
}

/**
 * Verifies that this [OpenEndRange] does not beWithin with a [ClosedRange].
 *
 * Assertion to check that this [OpenEndRange] does not beWithin with a [ClosedRange].
 *
 * An empty range will always fail. If you need to check for empty range, use [ClosedRange.shouldBeEmpty]
 *
 * @see [shouldNotbeWithin]
 * @see [beWithin]
 */
@OptIn(ExperimentalStdlibApi::class)
@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
inline infix fun <reified T: Comparable<T>> OpenEndRange<T>.shouldNotBeWithin(range: ClosedRange<T>): OpenEndRange<T> {
   when(T::class) {
      Int::class -> shouldNotBeWithinRangeOfInt(this as OpenEndRange<Int>, range as ClosedRange<Int>)
      Long::class -> shouldNotBeWithinRangeOfLong(this as OpenEndRange<Long>, range as ClosedRange<Long>)
      else -> Range.of(this) shouldNot beWithin(Range.of(range))
   }
   return this
}

/**
 * Verifies that this [OpenEndRange] does not beWithin with another [OpenEndRange].
 *
 * Assertion to check that this [OpenEndRange] does not beWithin with another [OpenEndRange].
 *
 * @see [shouldNotbeWithin]
 * @see [beWithin]
 */
@OptIn(ExperimentalStdlibApi::class)
infix fun <T: Comparable<T>> OpenEndRange<T>.shouldNotBeWithin(range: OpenEndRange<T>): OpenEndRange<T> {
   Range.of(this) shouldNot beWithin(Range.of(range))
   return this
}

/**
 *  Matcher that verifies that this [range] beWithins with another [range]
 *
 * Assertion to check that this [range] beWithins with another [range].
 *
 * An empty range will always fail. If you need to check for empty range, use [Iterable.shouldBeEmpty]
 *
 */
internal fun <T: Comparable<T>> beWithin(range: Range<T>) = object : Matcher<Range<T>> {
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

@OptIn(ExperimentalStdlibApi::class)
internal fun shouldBeWithinRangeOfInt(value: OpenEndRange<Int>,
   range: ClosedRange<Int>
){
   value should beWithinRangeOfInt(range)
}

@OptIn(ExperimentalStdlibApi::class)
internal fun shouldNotBeWithinRangeOfInt(value: OpenEndRange<Int>,
                                      range: ClosedRange<Int>
){
   value shouldNot beWithinRangeOfInt(range)
}

@OptIn(ExperimentalStdlibApi::class)
internal fun beWithinRangeOfInt(
   range: ClosedRange<Int>
) = object : Matcher<OpenEndRange<Int>> {
   override fun test(value: OpenEndRange<Int>): MatcherResult {
      return resultForWithin(range, value, (range.endInclusive + 1))
   }
}

@OptIn(ExperimentalStdlibApi::class)
internal fun shouldBeWithinRangeOfLong(value: OpenEndRange<Long>,
                             range: ClosedRange<Long>
){
   value should beWithinRangeOfLong(range)
}

@OptIn(ExperimentalStdlibApi::class)
internal fun shouldNotBeWithinRangeOfLong(value: OpenEndRange<Long>,
                                       range: ClosedRange<Long>
){
   value shouldNot beWithinRangeOfLong(range)
}

@OptIn(ExperimentalStdlibApi::class)
internal fun beWithinRangeOfLong(
   range: ClosedRange<Long>
) = object : Matcher<OpenEndRange<Long>> {
   override fun test(value: OpenEndRange<Long>): MatcherResult {
      return resultForWithin(range, value, (range.endInclusive + 1L))
   }
}

@OptIn(ExperimentalStdlibApi::class)
internal fun<T: Comparable<T>> resultForWithin(range: ClosedRange<T>, value: OpenEndRange<T>, valueAfterRangeEnd: T): MatcherResult {
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
