package io.kotest.matchers.date

import com.soywiz.klock.Date
import com.soywiz.klock.DateTime
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

infix fun Date.shouldHaveSameYear(date: Date) = this shouldBe haveSameYear(date)

infix fun Date.shouldNotHaveSameYear(date: Date) = this shouldNotBe haveSameYear(date)

fun haveSameYear(date: Date) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.year == date.year,
         { "$value should have year ${date.year}" },
         { "$value should not have year ${date.year}" }
      )
}

infix fun Date.shouldHaveSameYear(year: Long) = this shouldBe haveSameYear(year)

infix fun Date.shouldNotHaveSameYear(year: Long) = this shouldNotBe haveSameYear(year)

fun haveSameYear(year: Long) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.year.toLong() == year,
         { "$value should have year $year" },
         { "$value should not have year $year" }
      )
}

infix fun Date.shouldHaveSameMonth(date: Date) = this shouldBe haveSameMonth(date)

infix fun Date.shouldNotHaveSameMonth(date: Date) = this shouldNotBe haveSameMonth(date)

fun haveSameMonth(date: Date) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.month == date.month,
         { "$value should have month ${date.month}" },
         { "$value should not have month ${date.month}" }
      )
}

infix fun Date.shouldHaveSameMonth(month: Long) = this shouldBe haveSameMonth(month)

infix fun Date.shouldNotHaveSameMonth(month: Long) = this shouldNotBe haveSameMonth(month)

fun haveSameMonth(month: Long) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.month.index1.toLong() == month,
         { "$value should have month $month" },
         { "$value should not have month $month" }
      )
}

infix fun Date.shouldHaveSameDay(date: Date) = this shouldBe haveSameDay(date)

infix fun Date.shouldNotHaveSameDay(date: Date) = this shouldNotBe haveSameDay(date)

fun haveSameDay(date: Date) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.day == date.day,
         { "$value should have day ${date.day}" },
         { "$value should not have day ${date.day}" }
      )
}

infix fun Date.shouldHaveSameDay(day: Long) = this shouldBe haveSameMonth(day)

infix fun Date.shouldNotHaveSameDay(day: Long) = this shouldNotBe haveSameMonth(day)

fun haveSameDay(day: Long) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.day.toLong() == day,
         { "$value should have day $day" },
         { "$value should not have day $day" }
      )
}

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


