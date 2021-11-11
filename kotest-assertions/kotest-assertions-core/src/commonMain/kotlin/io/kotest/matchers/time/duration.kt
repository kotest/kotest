package io.kotest.matchers.time

import io.kotest.assertions.print.print
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.time.Duration
import kotlin.time.DurationUnit

infix fun Duration.shouldHaveSeconds(seconds: Long) = this should haveSeconds(seconds)

infix fun Duration.shouldNotHaveSeconds(seconds: Long) = this shouldNot haveSeconds(seconds)

fun haveSeconds(seconds: Long) = neverNullMatcher<Duration> { value ->
   MatcherResult(
      value.toLong(DurationUnit.SECONDS) == seconds,
      { "${value.print().value} should have $seconds seconds" },
      {
         "${value.print().value} should not have $seconds seconds"
      })
}


infix fun Duration.shouldHaveMillis(millis: Long) = this should haveMillis(millis)

infix fun Duration.shouldNotHaveMillis(millis: Long) = this shouldNot haveMillis(millis)


fun haveMillis(millis: Long) = neverNullMatcher<Duration> { value ->
   MatcherResult(
      value.toLong(DurationUnit.MILLISECONDS) == millis,
      { "${value.print().value} should have $millis millis" },
      {
         "${value.print().value} should not have $millis millis"
      })
}

infix fun Duration.shouldHaveMinutes(minutes: Long) = this should haveMinutes(minutes)

infix fun Duration.shouldNotHaveMinutes(minutes: Long) = this shouldNot haveMinutes(minutes)

fun haveMinutes(minutes: Long) = neverNullMatcher<Duration> { value ->
   MatcherResult(
      value.toLong(DurationUnit.MINUTES) == minutes,
      { "${value.print().value} should have $minutes minutes" },
      {
         "${value.print().value} should not have $minutes minutes"
      })
}


infix fun Duration.shouldHaveHours(hours: Long) = this should haveHours(hours)

infix fun Duration.shouldNotHaveHours(hours: Long) = this shouldNot haveHours(hours)


fun haveHours(hours: Long) = neverNullMatcher<Duration> { value ->
   MatcherResult(
      value.toLong(DurationUnit.HOURS) == hours,
      { "${value.print().value} should have $hours hours" },
      {
         "${value.print().value} should not have $hours hours"
      })
}


