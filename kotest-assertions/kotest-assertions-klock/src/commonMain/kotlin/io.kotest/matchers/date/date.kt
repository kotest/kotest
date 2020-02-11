package io.kotest.matchers.date

import com.soywiz.klock.Date
import com.soywiz.klock.DateTime
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

fun Date.shouldBeToday() = this shouldBe beToday()

fun Date.shouldNotBeToday() = this shouldNotBe beToday()

fun beToday() = object : Matcher<Date> {
   override fun test(value: Date): MatcherResult =
      MatcherResult(
         value == DateTime.now().date,
         "$value should be today",
         "$value should not be today"
      )
}

infix fun Date.shouldBeBefore(date: Date) = this shouldBe before(date)

fun before(date: Date): Matcher<Date> = object : Matcher<Date> {
   override fun test(value: Date): MatcherResult =
      MatcherResult(
         value < date,
         "$value should be before $date",
         "$value should not be before $date"
      )
}

infix fun Date.shouldBeAfter(date: Date) = this shouldBe after(date)

fun after(date: Date): Matcher<Date> = object : Matcher<Date> {
   override fun test(value: Date): MatcherResult =
      MatcherResult(
         value > date,
         "$value should be before $date",
         "$value should not be before $date"
      )
}

fun Date.shouldBeBetween(a: Date, b: Date) = this shouldBe between(a, b)

fun Date.shouldNotBeBetween(a: Date, b: Date) = this shouldNotBe between(a, b)

fun between(a: Date, b: Date): Matcher<Date> = object : Matcher<Date> {
   override fun test(value: Date): MatcherResult =
      MatcherResult(
         value > a && value < b,
         { "$value should be after $a and before $b" },
         { "$value should not be be after $a and before $b" }
      )
}


