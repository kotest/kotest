package io.kotest.matchers.date

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot
import java.sql.Timestamp

fun sameTimestamp(timestamp: Timestamp) = object: Matcher<Timestamp> {
   override fun test(value: Timestamp): MatcherResult {
      return MatcherResult(
         timestamp.equals(value),
         {"Expected $timestamp to be equal $value, but it's not."},
         {"$timestamp not expected to be equal $value, but it is equal"}
      )
   }
}

fun afterTimestamp(timestamp: Timestamp) = object: Matcher<Timestamp>  {
   override fun test(value: Timestamp): MatcherResult {
      return MatcherResult(
         value.after(timestamp),
         {"Expected $timestamp to be after $value, but it's not."},
         {"$timestamp is not expected to be after $value."}
      )
   }
}

fun beforeTimestamp(timestamp: Timestamp) = object: Matcher<Timestamp>  {
   override fun test(value: Timestamp): MatcherResult {
      return MatcherResult(
         value.before(timestamp),
         {"Expected $timestamp to be before $value, but it's not."},
         {"$timestamp is not expected to be before $value."}
      )
   }
}


/**
 * Assert that [Timestamp] is equal to [anotherTimestamp].
 * @see [shouldNotBe]
 * */
infix fun Timestamp.shouldBe(anotherTimestamp: Timestamp) = this should sameTimestamp(anotherTimestamp)

/**
 * Assert that [Timestamp] is not equal to [anotherTimestamp].
 * @see [shouldBe]
 * */
infix fun Timestamp.shouldNotBe(anotherTimestamp: Timestamp) = this shouldNot sameTimestamp(anotherTimestamp)

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
