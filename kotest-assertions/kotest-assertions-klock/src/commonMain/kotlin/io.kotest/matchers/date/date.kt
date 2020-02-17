package io.kotest.matchers.date

import com.soywiz.klock.Date
import com.soywiz.klock.DateTime
import io.kotest.matchers.*

infix fun Date.shouldHaveSameYearAs(date: Date) = this should haveSameYear(date)

infix fun Date.shouldNotHaveSameYearAs(date: Date) = this shouldNot haveSameYear(date)

fun haveSameYear(date: Date) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.year == date.year,
         { "$value should have year ${date.year}" },
         { "$value should not have year ${date.year}" }
      )
}

infix fun Date.shouldHaveYear(year: Int) = this should haveYear(year)

infix fun Date.shouldNotHaveYear(year: Int) = this shouldNot haveYear(year)

fun haveYear(year: Int) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.year == year,
         { "$value should have year $year" },
         { "$value should not have year $year" }
      )
}

infix fun Date.shouldHaveSameMonthAs(date: Date) = this should haveSameMonth(date)

infix fun Date.shouldNotHaveSameMonthAs(date: Date) = this shouldNot haveSameMonth(date)

fun haveSameMonth(date: Date) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.month == date.month,
         { "$value should have month ${date.month}" },
         { "$value should not have month ${date.month}" }
      )
}

infix fun Date.shouldHaveMonth(month: Int) = this should haveMonth(month)

infix fun Date.shouldNotHaveMonth(month: Int) = this shouldNot haveMonth(month)

fun haveMonth(month: Int) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.month.index1 == month,
         { "$value should have month $month" },
         { "$value should not have month $month" }
      )
}

infix fun Date.shouldHaveSameDayAs(date: Date) = this should haveSameDay(date)

infix fun Date.shouldNotHaveSameDayAs(date: Date) = this shouldNot haveSameDay(date)

fun haveSameDay(date: Date) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.day == date.day,
         { "$value should have day ${date.day}" },
         { "$value should not have day ${date.day}" }
      )
}

infix fun Date.shouldHaveDay(day: Int) = this should haveDay(day)

infix fun Date.shouldNotHaveDay(day: Int) = this shouldNot haveDay(day)

fun haveDay(day: Int) = object : Matcher<Date> {
   override fun test(value: Date) =
      MatcherResult(
         value.day == day,
         { "$value should have day $day" },
         { "$value should not have day $day" }
      )
}

fun Date.shouldBeToday() = this should beToday()

fun Date.shouldNotBeToday() = this shouldNot beToday()

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


