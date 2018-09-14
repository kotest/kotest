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

/**
 * Asserts that this year is the same as [date]'s year
 *
 * Verifies that this year is the same as [date]'s year, ignoring any other fields.
 * For example, 09/02/1998 has the same year as 10/03/1998, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDate.shouldNotHaveSameYearAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldHaveSameYearAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldHaveSameYearAsFailure]
 */
infix fun LocalDate.shouldHaveSameYearAs(date: LocalDate) = this should haveSameYear(date)

/**
 * Asserts that this year is NOT the same as [date]'s year
 *
 * Verifies that this year isn't the same as [date]'s year, ignoring any other fields.
 * For example, 09/02/1998 doesn't have the same year as 09/02/2018, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDate.shouldHaveSameYearAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldNotHaveSameYearAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldNotHaveSameYearAsFailure]
 */
infix fun LocalDate.shouldNotHaveSameYearAs(date: LocalDate) = this shouldNot haveSameYear(date)

/**
 * Matcher that compares years of LocalDates
 *
 * Verifies that two dates have exactly the same year, ignoring any other fields.
 * For example, 09/02/1998 has the same year as 10/03/1998, and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateHaveSameYear]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateHaveSameYearNegation]
 *
 * @see [LocalDate.shouldHaveSameYearAs]
 * @see [LocalDate.shouldNotHaveSameYearAs]
 */
fun haveSameYear(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

/**
 * Asserts that this year is the same as [date]'s year
 *
 * Verifies that this year is the same as [date]'s year, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 has the same year as 10/03/1998 11:30:30, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDateTime.shouldNotHaveSameYearAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldHaveSameYearAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldHaveSameYearAsFailure]
 */
infix fun LocalDateTime.shouldHaveSameYearAs(date: LocalDateTime) = this should haveSameYear(date)

/**
 * Asserts that this year is NOT the same as [date]'s year
 *
 * Verifies that this year isn't the same as [date]'s year, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 doesn't have the same year as 09/02/2018 10:00:00, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDateTime.shouldHaveSameYearAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldNotHaveSameYearAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldNotHaveSameYearAsFailure]
 */
infix fun LocalDateTime.shouldNotHaveSameYearAs(date: LocalDateTime) = this shouldNot haveSameYear(date)

/**
 * Matcher that compares years of LocalDateTimes
 *
 * Verifies that two DateTimes have exactly the same year, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 has the same year as 10/03/1998 11:30:30, and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeHaveSameYear]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeHaveSameYearNegation]
 *
 * @see [LocalDateTime.shouldHaveSameYearAs]
 * @see [LocalDateTime.shouldNotHaveSameYearAs]
 */
fun haveSameYear(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

/**
 * Asserts that this year is the same as [date]'s year
 *
 * Verifies that this year is the same as [date]'s year, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo has the same year as 10/03/1998 11:30:30 -05:00 America/Chicago,
 * and this assertion should pass for this comparison
 *
 * Opposite of [ZonedDateTime.shouldNotHaveSameYearAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldHaveSameYearAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldHaveSameYearAsFailure]
 */
infix fun ZonedDateTime.shouldHaveSameYearAs(date: ZonedDateTime) = this should haveSameYear(date)

/**
 * Asserts that this year is NOT the same as [date]'s year
 *
 * Verifies that this year isn't the same as [date]'s year, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo doesn't have the same year as 09/02/2018 10:00:00 -03:00 America/Sao_Paulo,
 * and this assertion should pass for this comparison
 *
 * Opposite of [ZonedDateTime.shouldHaveSameYearAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldNotHaveSameYearAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldNotHaveSameYearAsFailure]
 */
infix fun ZonedDateTime.shouldNotHaveSameYearAs(date: ZonedDateTime) = this shouldNot haveSameYear(date)

/**
 * Matcher that compares years of ZonedDateTimes
 *
 * Verifies that two ZonedDateTimes have exactly the same year, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo has the same year as 10/03/1998 11:30:30 -05:00 America/Chicago,
 * and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeHaveSameYear]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeHaveSameYearNegation]
 *
 * @see [ZonedDateTime.shouldHaveSameYearAs]
 * @see [ZonedDateTime.shouldNotHaveSameYearAs]
 */
fun haveSameYear(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

/**
 * Asserts that this year is the same as [date]'s year
 *
 * Verifies that this year is the same as [date]'s year, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 has the same year as 10/03/1998 11:30:30 -05:00,
 * and this assertion should pass for this comparison
 *
 * Opposite of [OffsetDateTime.shouldNotHaveSameYearAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldHaveSameYearAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldHaveSameYearAsFailure]
 */
infix fun OffsetDateTime.shouldHaveSameYearAs(date: OffsetDateTime) = this should haveSameYear(date)

/**
 * Asserts that this year is NOT the same as [date]'s year
 *
 * Verifies that this year isn't the same as [date]'s year, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 doesn't have the same year as 09/02/2018 10:00:00 -03:00,
 * and this assertion should pass for this comparison
 *
 * Opposite of [OffsetDateTime.shouldHaveSameYearAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldNotHaveSameYearAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldNotHaveSameYearAsFailure]
 */
infix fun OffsetDateTime.shouldNotHaveSameYearAs(date: OffsetDateTime) = this shouldNot haveSameYear(date)

/**
 * Matcher that compares years of OffsetDateTimes
 *
 * Verifies that two ZonedDateTimes have exactly the same year, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 has the same year as 10/03/1998 11:30:30 -05:00,
 * and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeHaveSameYear]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeHaveSameYearNegation]
 *
 * @see [OffsetDateTime.shouldHaveSameYearAs]
 * @see [OffsetDateTime.shouldNotHaveSameYearAs]
 */
fun haveSameYear(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

infix fun LocalDate.shouldHaveSameMonthAs(date: LocalDate) = this should haveSameMonth(date)
infix fun LocalDate.shouldNotHaveSameMonthAs(date: LocalDate) = this shouldNot haveSameMonth(date)
fun haveSameMonth(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

infix fun LocalDateTime.shouldHaveSameMonthAs(date: LocalDateTime) = this should haveSameMonth(date)
infix fun LocalDateTime.shouldNotHaveSameMonthAs(date: LocalDateTime) = this shouldNot haveSameMonth(date)
fun haveSameMonth(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

infix fun ZonedDateTime.shouldHaveSameMonthAs(date: ZonedDateTime) = this should haveSameMonth(date)
infix fun ZonedDateTime.shouldNotHaveSameMonthAs(date: ZonedDateTime) = this shouldNot haveSameMonth(date)
fun haveSameMonth(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

infix fun OffsetDateTime.shouldHaveSameMonthAs(date: OffsetDateTime) = this should haveSameMonth(date)
infix fun OffsetDateTime.shouldNotHaveSameMonthAs(date: OffsetDateTime) = this shouldNot haveSameMonth(date)
fun haveSameMonth(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

infix fun LocalDate.shouldHaveSameDayAs(date: LocalDate) = this should haveSameDay(date)
infix fun LocalDate.shouldNotHaveSameDayAs(date: LocalDate) = this shouldNot haveSameDay(date)
fun haveSameDay(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

infix fun LocalDateTime.shouldHaveSameDayAs(date: LocalDateTime) = this should haveSameDay(date)
infix fun LocalDateTime.shouldNotHaveSameDayAs(date: LocalDateTime) = this shouldNot haveSameDay(date)
fun haveSameDay(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

infix fun ZonedDateTime.shouldHaveSameDayAs(date: ZonedDateTime) = this should haveSameDay(date)
infix fun ZonedDateTime.shouldNotHaveSameDayAs(date: ZonedDateTime) = this shouldNot haveSameDay(date)
fun haveSameDay(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

infix fun OffsetDateTime.shouldHaveSameDayAs(date: OffsetDateTime) = this should haveSameDay(date)
infix fun OffsetDateTime.shouldNotHaveSameDayAs(date: OffsetDateTime) = this shouldNot haveSameDay(date)
fun haveSameDay(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

infix fun LocalDate.shouldBeBefore(date: LocalDate) = this should before(date)
infix fun LocalDate.shouldNotBeBefore(date: LocalDate) = this shouldNot before(date)
fun before(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.isBefore(date), "$value should be before $date", "$value should not be before $date")
}

infix fun LocalDateTime.shouldBeBefore(date: LocalDateTime) = this should before(date)
infix fun LocalDateTime.shouldNotBeBefore(date: LocalDateTime) = this shouldNot before(date)
fun before(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.isBefore(date), "$value should be before $date", "$value should not be before $date")
}

infix fun ZonedDateTime.shouldBeBefore(date: ZonedDateTime) = this should before(date)
infix fun ZonedDateTime.shouldNotBeBefore(date: ZonedDateTime) = this shouldNot before(date)
fun before(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.isBefore(date), "$value should be before $date", "$value should not be before $date")
}

infix fun OffsetDateTime.shouldBeBefore(date: OffsetDateTime) = this should before(date)
infix fun OffsetDateTime.shouldNotBeBefore(date: OffsetDateTime) = this shouldNot before(date)
fun before(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.isBefore(date), "$value should be before $date", "$value should not be before $date")
}

infix fun LocalDate.shouldBeAfter(date: LocalDate) = this should after(date)
infix fun LocalDate.shouldNotBeAfter(date: LocalDate) = this shouldNot after(date)
fun after(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.isAfter(date), "$value should be after $date", "$value should not be after $date")
}

infix fun LocalDateTime.shouldBeAfter(date: LocalDateTime) = this should after(date)
infix fun LocalDateTime.shouldNotBeAfter(date: LocalDateTime) = this shouldNot after(date)
fun after(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.isAfter(date), "$value should be after $date", "$value should not be after $date")
}

infix fun ZonedDateTime.shouldBeAfter(date: ZonedDateTime) = this should after(date)
infix fun ZonedDateTime.shouldNotBeAfter(date: ZonedDateTime) = this shouldNot after(date)
fun after(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.isAfter(date), "$value should be after $date", "$value should not be after $date")
}

infix fun OffsetDateTime.shouldBeAfter(date: OffsetDateTime) = this should after(date)
infix fun OffsetDateTime.shouldNotBeAfter(date: OffsetDateTime) = this shouldNot after(date)
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