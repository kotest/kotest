package io.kotest.matchers.time

import io.kotest.assertions.show.show
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
infix fun Duration.shouldHaveSeconds(seconds: Long) = this should haveSeconds(seconds)

@OptIn(ExperimentalTime::class)
infix fun Duration.shouldNotHaveSeconds(seconds: Long) = this shouldNot haveSeconds(seconds)

@OptIn(ExperimentalTime::class)
fun haveSeconds(seconds: Long) = neverNullMatcher<Duration> { value ->
   MatcherResult(
      value.toLong(DurationUnit.SECONDS) == seconds,
      "${value.show().value} should have $seconds seconds",
      "${value.show().value} should not have $seconds seconds"
   )
}


@OptIn(ExperimentalTime::class)
infix fun Duration.shouldHaveMillis(millis: Long) = this should haveMillis(millis)

@OptIn(ExperimentalTime::class)
infix fun Duration.shouldNotHaveMillis(millis: Long) = this shouldNot haveMillis(millis)


@OptIn(ExperimentalTime::class)
fun haveMillis(millis: Long) = neverNullMatcher<Duration> { value ->
   MatcherResult(
      value.toLong(DurationUnit.MILLISECONDS) == millis,
      "${value.show().value} should have $millis millis",
      "${value.show().value} should not have $millis millis"
   )
}

@OptIn(ExperimentalTime::class)
infix fun Duration.shouldHaveMinutes(minutes: Long) = this should haveMinutes(minutes)

@OptIn(ExperimentalTime::class)
infix fun Duration.shouldNotHaveMinutes(minutes: Long) = this shouldNot haveMinutes(minutes)

@OptIn(ExperimentalTime::class)
fun haveMinutes(minutes: Long) = neverNullMatcher<Duration> { value ->
   MatcherResult(
      value.toLong(DurationUnit.MINUTES) == minutes,
      "${value.show().value} should have $minutes minutes",
      "${value.show().value} should not have $minutes minutes"
   )
}


@OptIn(ExperimentalTime::class)
infix fun Duration.shouldHaveHours(hours: Long) = this should haveHours(hours)

@OptIn(ExperimentalTime::class)
infix fun Duration.shouldNotHaveHours(hours: Long) = this shouldNot haveHours(hours)


@OptIn(ExperimentalTime::class)
fun haveHours(hours: Long) = neverNullMatcher<Duration> { value ->
   MatcherResult(
      value.toLong(DurationUnit.HOURS) == hours,
      "${value.show().value} should have $hours hours",
      "${value.show().value} should not have $hours hours"
   )
}


