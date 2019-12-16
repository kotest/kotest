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
         {"Expected $timestamp to be equal $value, but its not."},
         {"$timestamp expected to be equal $value, but it is equal"}
      )
   }
}

fun afterTimestamp(timestamp: Timestamp) = object: Matcher<Timestamp>  {
   override fun test(value: Timestamp): MatcherResult {
      return MatcherResult(
         value.after(timestamp),
         {"Expected $timestamp to be equal $value, but its not."},
         {"$timestamp expected to be equal $value, but it is equal"}
      )
   }
}

fun beforeTimestamp(timestamp: Timestamp) = object: Matcher<Timestamp>  {
   override fun test(value: Timestamp): MatcherResult {
      return MatcherResult(
         value.before(timestamp),
         {"Expected $timestamp to be equal $value, but its not."},
         {"$timestamp expected to be equal $value, but it is equal"}
      )
   }
}

infix fun Timestamp.shouldBe(timestamp: Timestamp) = this should sameTimestamp(timestamp)

infix fun Timestamp.shouldNotBe(timestamp: Timestamp) = this shouldNot sameTimestamp(timestamp)

infix fun Timestamp.shouldBeAfter(timestamp: Timestamp) = this should afterTimestamp(timestamp)

infix fun Timestamp.shouldBeBefore(timestamp: Timestamp) = this should beforeTimestamp(timestamp)
