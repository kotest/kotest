package io.kotest.matchers.date

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot
import java.sql.Timestamp

fun afterTimestamp(timestamp: Timestamp) = object: Matcher<Timestamp>  {
   override fun test(value: Timestamp): MatcherResult {
      return MatcherResult(
         value.after(timestamp),
         {"Expected $value to be after $timestamp, but it's not."},
         {"$value is not expected to be after $timestamp."}
      )
   }
}

fun beforeTimestamp(timestamp: Timestamp) = object: Matcher<Timestamp>  {
   override fun test(value: Timestamp): MatcherResult {
      return MatcherResult(
         value.before(timestamp),
         {"Expected $value to be before $timestamp, but it's not."},
         {"$value is not expected to be before $timestamp."}
      )
   }
}

/**
 * Assert that [Timestamp] is after [anotherTimestamp].
 * @see [shouldNotBeAfter]
 * */
infix fun Timestamp.shouldBeAfter(anotherTimestamp: Timestamp) = this should afterTimestamp(anotherTimestamp)

/**
 * Assert that [Timestamp] is not after [anotherTimestamp].
 * @see [shouldBeAfter]
 * */
infix fun Timestamp.shouldNotBeAfter(anotherTimestamp: Timestamp) = this shouldNot afterTimestamp(anotherTimestamp)

/**
 * Assert that [Timestamp] is before [anotherTimestamp].
 * @see [shouldNotBeBefore]
 * */
infix fun Timestamp.shouldBeBefore(anotherTimestamp: Timestamp) = this should beforeTimestamp(anotherTimestamp)

/**
 * Assert that [Timestamp] is not before [anotherTimestamp].
 * @see [shouldBeBefore]
 * */
infix fun Timestamp.shouldNotBeBefore(anotherTimestamp: Timestamp) = this shouldNot beforeTimestamp(anotherTimestamp)
