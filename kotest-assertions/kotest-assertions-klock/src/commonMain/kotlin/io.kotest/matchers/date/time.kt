package io.kotest.matchers.date

import com.soywiz.klock.Time
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun Time.shouldHaveSameHoursAs(time: Time) = this should haveSameHours(time)

infix fun Time.shouldNotHaveSameHoursAs(time: Time) = this shouldNot haveSameHours(time)

fun haveSameHours(time: Time): Matcher<Time> = object : Matcher<Time> {
   override fun test(value: Time): MatcherResult =
      MatcherResult(
         value.hour == time.hour,
         { "$value should have hours ${time.hour}" },
         { "$value should not have hours ${time.hour}" }
      )
}

infix fun Time.shouldHaveHours(hours: Long) = this should haveHours(hours)

infix fun Time.shouldNotHaveHours(hours: Long) = this shouldNot haveHours(hours)

fun haveHours(hours: Long): Matcher<Time> = object : Matcher<Time> {
   override fun test(value: Time): MatcherResult =
      MatcherResult(
         value.hour.toLong() == hours,
         { "$value should have hours $hours" },
         { "$value should not have hours $hours" }
      )
}

infix fun Time.shouldHaveSameMinutesAs(time: Time) = this should haveSameMinutes(time)

infix fun Time.shouldNotHaveSameMinutesAs(time: Time) = this shouldNot haveSameMinutes(time)

fun haveSameMinutes(time: Time): Matcher<Time> = object : Matcher<Time> {
   override fun test(value: Time): MatcherResult =
      MatcherResult(
         value.minute == time.minute,
         { "$value should have minutes ${time.minute}" },
         { "$value should not have minutes ${time.minute}" }
      )
}

infix fun Time.shouldHaveMinutes(minutes: Long) = this should haveMinutes(minutes)

infix fun Time.shouldNotHaveMinutes(minutes: Long) = this shouldNot haveMinutes(minutes)

fun haveMinutes(minutes: Long): Matcher<Time> = object : Matcher<Time> {
   override fun test(value: Time): MatcherResult =
      MatcherResult(
         value.minute.toLong() == minutes,
         { "$value should have minutes $minutes" },
         { "$value should not have minutes $minutes" }
      )
}

infix fun Time.shouldHaveSameSecondsAs(time: Time) = this should haveSameSeconds(time)

infix fun Time.shouldNotHaveSameSecondsAs(time: Time) = this shouldNot haveSameSeconds(time)

fun haveSameSeconds(time: Time): Matcher<Time> = object : Matcher<Time> {
   override fun test(value: Time): MatcherResult =
      MatcherResult(
         value.second == time.second,
         { "$value should have seconds ${time.second}" },
         { "$value should not have seconds ${time.second}" }
      )
}

infix fun Time.shouldHaveSeconds(seconds: Long) = this should haveSeconds(seconds)

infix fun Time.shouldNotSameSeconds(seconds: Long) = this shouldNot haveSeconds(seconds)

fun haveSeconds(seconds: Long): Matcher<Time> = object : Matcher<Time> {
   override fun test(value: Time): MatcherResult =
      MatcherResult(
         value.second.toLong() == seconds,
         { "$value should have seconds $seconds" },
         { "$value should not have seconds $seconds" }
      )
}

infix fun Time.shouldHaveSameMillisecondsAs(time: Time) = this should haveSameMilliseconds(time)

infix fun Time.shouldNotHaveSameMillisecondsAs(time: Time) = this shouldNot haveSameMilliseconds(time)

fun haveSameMilliseconds(time: Time): Matcher<Time> = object : Matcher<Time> {
   override fun test(value: Time): MatcherResult =
      MatcherResult(
         value.millisecond == time.millisecond,
         { "$value should have milliseconds ${time.millisecond}" },
         { "$value should not have milliseconds ${time.millisecond}" }
      )
}

infix fun Time.shouldHaveMilliseconds(millis: Long) = this should haveMilliseconds(millis)

infix fun Time.shouldNotHaveMilliseconds(millis: Long) = this shouldNot haveMilliseconds(millis)

fun haveMilliseconds(millis: Long): Matcher<Time> = object : Matcher<Time> {
   override fun test(value: Time): MatcherResult =
      MatcherResult(
         value.millisecond.toLong() == millis,
         { "$value should have milliseconds $millis" },
         { "$value should not have milliseconds $millis" }
      )
}

infix fun Time.shouldBeBefore(time: Time) = this should before(time)

infix fun Time.shouldNotBeBefore(time: Time) = this shouldNot before(time)

fun before(time: Time): Matcher<Time> = object : Matcher<Time> {
   override fun test(value: Time): MatcherResult =
      MatcherResult(
         value < time,
         { "$value should be before $time" },
         { "$value should not be before $time" }
      )
}

infix fun Time.shouldBeAfter(time: Time) = this should after(time)

infix fun Time.shouldNotBeAfter(time: Time) = this shouldNot after(time)

fun after(time: Time): Matcher<Time> = object : Matcher<Time> {
   override fun test(value: Time): MatcherResult =
      MatcherResult(
         value > time,
         { "$value should be after $time" },
         { "$value should not be after $time" }
      )
}
