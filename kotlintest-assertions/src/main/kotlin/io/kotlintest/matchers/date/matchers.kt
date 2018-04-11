package io.kotlintest.matchers.date

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZonedDateTime

fun LocalDate.shouldHaveSameYearAs(date: LocalDate) = this should haveSameYear(date)
fun LocalDate.shouldNotHaveSameYearAs(date: LocalDate) = this shouldNot haveSameYear(date)
fun haveSameYear(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

fun LocalDateTime.shouldHaveSameYearAs(date: LocalDateTime) = this should haveSameYear(date)
fun LocalDateTime.shouldNotHaveSameYearAs(date: LocalDateTime) = this shouldNot haveSameYear(date)
fun haveSameYear(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

fun ZonedDateTime.shouldHaveSameYearAs(date: ZonedDateTime) = this should haveSameYear(date)
fun ZonedDateTime.shouldNotHaveSameYearAs(date: ZonedDateTime) = this shouldNot haveSameYear(date)
fun haveSameYear(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

fun OffsetDateTime.shouldHaveSameYearAs(date: OffsetDateTime) = this should haveSameYear(date)
fun OffsetDateTime.shouldNotHaveSameYearAs(date: OffsetDateTime) = this shouldNot haveSameYear(date)
fun haveSameYear(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

fun LocalDate.shouldHaveSameMonthAs(date: LocalDate) = this should haveSameMonth(date)
fun LocalDate.shouldNotHaveSameMonthAs(date: LocalDate) = this shouldNot haveSameMonth(date)
fun haveSameMonth(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

fun LocalDateTime.shouldHaveSameMonthAs(date: LocalDateTime) = this should haveSameMonth(date)
fun LocalDateTime.shouldNotHaveSameMonthAs(date: LocalDateTime) = this shouldNot haveSameMonth(date)
fun haveSameMonth(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

fun ZonedDateTime.shouldHaveSameMonthAs(date: ZonedDateTime) = this should haveSameMonth(date)
fun ZonedDateTime.shouldNotHaveSameMonthAs(date: ZonedDateTime) = this shouldNot haveSameMonth(date)
fun haveSameMonth(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

fun OffsetDateTime.shouldHaveSameMonthAs(date: OffsetDateTime) = this should haveSameMonth(date)
fun OffsetDateTime.shouldNotHaveSameMonthAs(date: OffsetDateTime) = this shouldNot haveSameMonth(date)
fun haveSameMonth(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

fun LocalDate.shouldHaveSameDayAs(date: LocalDate) = this should haveSameDay(date)
fun LocalDate.shouldNotHaveSameDayAs(date: LocalDate) = this shouldNot haveSameDay(date)
fun haveSameDay(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

fun LocalDateTime.shouldHaveSameDayAs(date: LocalDateTime) = this should haveSameDay(date)
fun LocalDateTime.shouldNotHaveSameDayAs(date: LocalDateTime) = this shouldNot haveSameDay(date)
fun haveSameDay(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

fun ZonedDateTime.shouldHaveSameDayAs(date: ZonedDateTime) = this should haveSameDay(date)
fun ZonedDateTime.shouldNotHaveSameDayAs(date: ZonedDateTime) = this shouldNot haveSameDay(date)
fun haveSameDay(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

fun OffsetDateTime.shouldHaveSameDayAs(date: OffsetDateTime) = this should haveSameDay(date)
fun OffsetDateTime.shouldNotHaveSameDayAs(date: OffsetDateTime) = this shouldNot haveSameDay(date)
fun haveSameDay(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

fun LocalDate.shouldBeBefore(date: LocalDate) = this should before(date)
fun LocalDate.shouldNotBeBefore(date: LocalDate) = this shouldNot before(date)
fun before(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.isBefore(date), "$value should be before $date", "$value should not be before $date")
}

fun LocalDateTime.shouldBeBefore(date: LocalDateTime) = this should before(date)
fun LocalDateTime.shouldNotBeBefore(date: LocalDateTime) = this shouldNot before(date)
fun before(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.isBefore(date), "$value should be before $date", "$value should not be before $date")
}

fun ZonedDateTime.shouldBeBefore(date: ZonedDateTime) = this should before(date)
fun ZonedDateTime.shouldNotBeBefore(date: ZonedDateTime) = this shouldNot before(date)
fun before(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.isBefore(date), "$value should be before $date", "$value should not be before $date")
}

fun OffsetDateTime.shouldBeBefore(date: OffsetDateTime) = this should before(date)
fun OffsetDateTime.shouldNotBeBefore(date: OffsetDateTime) = this shouldNot before(date)
fun before(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.isBefore(date), "$value should be before $date", "$value should not be before $date")
}

fun LocalDate.shouldBeAfter(date: LocalDate) = this should after(date)
fun LocalDate.shouldNotBeAfter(date: LocalDate) = this shouldNot after(date)
fun after(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.isAfter(date), "$value should be after $date", "$value should not be after $date")
}

fun LocalDateTime.shouldBeAfter(date: LocalDateTime) = this should after(date)
fun LocalDateTime.shouldNotBeAfter(date: LocalDateTime) = this shouldNot after(date)
fun after(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.isAfter(date), "$value should be after $date", "$value should not be after $date")
}

fun ZonedDateTime.shouldBeAfter(date: ZonedDateTime) = this should after(date)
fun ZonedDateTime.shouldNotBeAfter(date: ZonedDateTime) = this shouldNot after(date)
fun after(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.isAfter(date), "$value should be after $date", "$value should not be after $date")
}

fun OffsetDateTime.shouldBeAfter(date: OffsetDateTime) = this should after(date)
fun OffsetDateTime.shouldNotBeAfter(date: OffsetDateTime) = this shouldNot after(date)
fun after(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.isAfter(date), "$value should be after $date", "$value should not be after $date")
}

fun LocalDate.shouldBeWithin(period: Period, date: LocalDate) = this should within(period, date)
fun LocalDate.shouldNotBeWithin(period: Period, date: LocalDate) = this shouldNot within(period, date)
fun within(period: Period, date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result {
    val start = date.minus(period)
    val end = date.plus(period)
    val passed = start == value || end == value || start.isBefore(value) && end.isAfter(value)
    return Result(passed, "$value should be within $period of $date", "$value should not be within $period of $date")
  }
}

fun LocalDateTime.shouldBeWithin(period: Period, date: LocalDateTime) = this should within(period, date)
fun LocalDateTime.shouldNotBeWithin(period: Period, date: LocalDateTime) = this shouldNot within(period, date)
fun within(period: Period, date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result {
    val start = date.minus(period)
    val end = date.plus(period)
    val passed = start == value || end == value || start.isBefore(value) && end.isAfter(value)
    return Result(passed, "$value should be within $period of $date", "$value should not be within $period of $date")
  }
}

fun ZonedDateTime.shouldBeWithin(period: Period, date: ZonedDateTime) = this should within(period, date)
fun ZonedDateTime.shouldNotBeWithin(period: Period, date: ZonedDateTime) = this shouldNot within(period, date)
fun within(period: Period, date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result {
    val start = date.minus(period)
    val end = date.plus(period)
    val passed = start == value || end == value || start.isBefore(value) && end.isAfter(value)
    return Result(passed, "$value should be within $period of $date", "$value should not be within $period of $date")
  }
}

fun OffsetDateTime.shouldBeWithin(period: Period, date: OffsetDateTime) = this should within(period, date)
fun OffsetDateTime.shouldNotBeWithin(period: Period, date: OffsetDateTime) = this shouldNot within(period, date)
fun within(period: Period, date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result {
    val start = date.minus(period)
    val end = date.plus(period)
    val passed = start == value || end == value || start.isBefore(value) && end.isAfter(value)
    return Result(passed, "$value should be within $period of $date", "$value should not be within $period of $date")
  }
}