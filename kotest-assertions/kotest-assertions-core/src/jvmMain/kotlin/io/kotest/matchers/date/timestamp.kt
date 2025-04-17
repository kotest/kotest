package io.kotest.matchers.date

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.sql.Timestamp

fun beAfter(timestamp: Timestamp) = object: Matcher<Timestamp> {
   override fun test(value: Timestamp): MatcherResult {
      return MatcherResult(
         value.after(timestamp),
         { "Expected $value to be after $timestamp, but it's not." },
         { "$value is not expected to be after $timestamp." }
      )
   }
}

fun beBefore(timestamp: Timestamp) = object: Matcher<Timestamp> {
   override fun test(value: Timestamp): MatcherResult {
      return MatcherResult(
         value.before(timestamp),
         { "Expected $value to be before $timestamp, but it's not." },
         { "$value is not expected to be before $timestamp." }
      )
   }
}

fun beBetween(fromTimestamp: Timestamp, toTimestamp: Timestamp) = object : Matcher<Timestamp> {
   override fun test(value: Timestamp): MatcherResult {
      return MatcherResult(
         value.after(fromTimestamp) && value.before(toTimestamp),
         { "$value should be after $fromTimestamp and before $toTimestamp" },
         { "$value should not be be after $fromTimestamp and before $toTimestamp" }
      )
   }
}

/**
 * Assert that [Timestamp] is after [anotherTimestamp].
 * @see [shouldNotBeAfter]
 * */
infix fun Timestamp.shouldBeAfter(anotherTimestamp: Timestamp) = this should beAfter(anotherTimestamp)

/**
 * Assert that [Timestamp] is not after [anotherTimestamp].
 * @see [shouldBeAfter]
 * */
infix fun Timestamp.shouldNotBeAfter(anotherTimestamp: Timestamp) = this shouldNot beAfter(anotherTimestamp)

/**
 * Assert that [Timestamp] is before [anotherTimestamp].
 * @see [shouldNotBeBefore]
 * */
infix fun Timestamp.shouldBeBefore(anotherTimestamp: Timestamp) = this should beBefore(anotherTimestamp)

/**
 * Assert that [Timestamp] is not before [anotherTimestamp].
 * @see [shouldBeBefore]
 * */
infix fun Timestamp.shouldNotBeBefore(anotherTimestamp: Timestamp) = this shouldNot beBefore(anotherTimestamp)

/**
 * Assert that [Timestamp] is between [fromTimestamp] and [toTimestamp].
 * @see [shouldNotBeBetween]
 * */
fun Timestamp.shouldBeBetween(fromTimestamp: Timestamp, toTimestamp: Timestamp) = this should beBetween(fromTimestamp, toTimestamp)

/**
 * Assert that [Timestamp] is not between [fromTimestamp] and [toTimestamp].
 * @see [shouldNotBeBetween]
 * */
fun Timestamp.shouldNotBeBetween(fromTimestamp: Timestamp, toTimestamp: Timestamp) = this shouldNot beBetween(fromTimestamp, toTimestamp)
