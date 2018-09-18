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

/**
 * Asserts that this month is the same as [date]'s month
 *
 * Verifies that month year is the same as [date]'s month, ignoring any other fields.
 * For example, 09/02/1998 has the same month as 10/02/2018, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDate.shouldNotHaveSameMonthAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldHaveSameMonthAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldHaveSameMonthAsFailure]
 */
infix fun LocalDate.shouldHaveSameMonthAs(date: LocalDate) = this should haveSameMonth(date)

/**
 * Asserts that this month is NOT the same as [date]'s month
 *
 * Verifies that this month isn't the same as [date]'s month, ignoring any other fields.
 * For example, 09/02/1998 doesn't have the same month as 09/03/1998, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDate.shouldHaveSameMonthAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldNotHaveSameMonthAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldNotHaveSameMonthAsFailure]
 */
infix fun LocalDate.shouldNotHaveSameMonthAs(date: LocalDate) = this shouldNot haveSameMonth(date)

/**
 * Matcher that compares months of LocalDates
 *
 * Verifies that two dates have exactly the same month, ignoring any other fields.
 * For example, 09/02/1998 has the same month as 10/02/2018, and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateHaveSameMonth]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateHaveSameMonthNegation]
 *
 * @see [LocalDate.shouldHaveSameMonthAs]
 * @see [LocalDate.shouldNotHaveSameMonthAs]
 */
fun haveSameMonth(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

/**
 * Asserts that this month is the same as [date]'s month
 *
 * Verifies that this month is the same as [date]'s month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 has the same month as 10/02/2018 11:30:30, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDateTime.shouldNotHaveSameMonthAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldHaveSameMonthAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldHaveSameMonthAsFailure]
 */
infix fun LocalDateTime.shouldHaveSameMonthAs(date: LocalDateTime) = this should haveSameMonth(date)

/**
 * Asserts that this month is NOT the same as [date]'s month
 *
 * Verifies that this month isn't the same as [date]'s month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 doesn't have the same month as 09/03/1998 10:00:00, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDateTime.shouldHaveSameMonthAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldNotHaveSameMonthAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldNotHaveSameMonthAsFailure]
 */
infix fun LocalDateTime.shouldNotHaveSameMonthAs(date: LocalDateTime) = this shouldNot haveSameMonth(date)

/**
 * Matcher that compares months of LocalDateTimes
 *
 * Verifies that two DateTimes have exactly the same month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 has the same month as 10/02/2018 11:30:30, and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeHaveSameMonth]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeHaveSameMonthNegation]
 *
 * @see [LocalDateTime.shouldHaveSameMonthAs]
 * @see [LocalDateTime.shouldNotHaveSameMonthAs]
 */
fun haveSameMonth(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

/**
 * Asserts that this month is the same as [date]'s month
 *
 * Verifies that this month is the same as [date]'s month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo has the same month as 10/02/2018 11:30:30 -05:00 America/Chicago,
 * and this assertion should pass for this comparison
 *
 * Opposite of [ZonedDateTime.shouldNotHaveSameMonthAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldHaveSameMonthAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldHaveSameMonthAsFailure]
 */
infix fun ZonedDateTime.shouldHaveSameMonthAs(date: ZonedDateTime) = this should haveSameMonth(date)

/**
 * Asserts that this month is NOT the same as [date]'s month
 *
 * Verifies that this month isn't the same as [date]'s month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo doesn't have the same month as 09/03/1998 10:00:00 -03:00 America/Sao_Paulo,
 * and this assertion should pass for this comparison
 *
 * Opposite of [ZonedDateTime.shouldHaveSameMonthAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldNotHaveSameMonthAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldNotHaveSameMonthAsFailure]
 */
infix fun ZonedDateTime.shouldNotHaveSameMonthAs(date: ZonedDateTime) = this shouldNot haveSameMonth(date)

/**
 * Matcher that compares months of ZonedDateTimes
 *
 * Verifies that two ZonedDateTimes have exactly the same month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo has the same month as 10/02/2018 11:30:30 -05:00 America/Chicago,
 * and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeHaveSameMonth]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeHaveSameMonthNegation]
 *
 * @see [ZonedDateTime.shouldHaveSameMonthAs]
 * @see [ZonedDateTime.shouldNotHaveSameMonthAs]
 */
fun haveSameMonth(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

/**
 * Asserts that this month is the same as [date]'s month
 *
 * Verifies that this month is the same as [date]'s month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 has the same month as 10/02/2018 11:30:30 -05:00,
 * and this assertion should pass for this comparison
 *
 * Opposite of [OffsetDateTime.shouldNotHaveSameMonthAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldHaveSameMonthAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldHaveSameMonthAsFailure]
 */
infix fun OffsetDateTime.shouldHaveSameMonthAs(date: OffsetDateTime) = this should haveSameMonth(date)

/**
 * Asserts that this month is NOT the same as [date]'s month
 *
 * Verifies that this month isn't the same as [date]'s month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 doesn't have the same month as 09/03/1998 10:00:00 -03:00,
 * and this assertion should pass for this comparison
 *
 * Opposite of [OffsetDateTime.shouldHaveSameMonthAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldNotHaveSameMonthAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldNotHaveSameMonthAsFailure]
 */
infix fun OffsetDateTime.shouldNotHaveSameMonthAs(date: OffsetDateTime) = this shouldNot haveSameMonth(date)


/**
 * Matcher that compares months of OffsetDateTimes
 *
 * Verifies that two ZonedDateTimes have exactly the same month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 has the same month as 10/02/1998 11:30:30 -05:00,
 * and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeHaveSameMonth]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeHaveSameMonthNegation]
 *
 * @see [OffsetDateTime.shouldHaveSameMonthAs]
 * @see [OffsetDateTime.shouldNotHaveSameMonthAs]
 */
fun haveSameMonth(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

/**
 * Asserts that this day is the same as [date]'s day
 *
 * Verifies that this day is the same as [date]'s day, ignoring any other fields.
 * For example, 09/02/1998 has the same day as 09/03/2018, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDate.shouldNotHaveSameDayAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldHaveSameDayAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldHaveSameDayAsFailure]
 */
infix fun LocalDate.shouldHaveSameDayAs(date: LocalDate) = this should haveSameDay(date)

/**
 * Asserts that this day is NOT the same as [date]'s day
 *
 * Verifies that this day isn't the same as [date]'s day, ignoring any other fields.
 * For example, 09/02/1998 doesn't have the same day as 10/02/1998, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDate.shouldHaveSameDayAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldNotHaveSameDayAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldNotHaveSameDayAsFailure]
 */
infix fun LocalDate.shouldNotHaveSameDayAs(date: LocalDate) = this shouldNot haveSameDay(date)

/**
 * Matcher that compares days of LocalDates
 *
 * Verifies that two dates have exactly the same day, ignoring any other fields.
 * For example, 09/02/1998 has the same day as 09/03/2018, and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateHaveSameDay]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateHaveSameDayNegation]
 *
 * @see [LocalDate.shouldHaveSameDayAs]
 * @see [LocalDate.shouldNotHaveSameDayAs]
 */
fun haveSameDay(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}


/**
 * Asserts that this day is the same as [date]'s day
 *
 * Verifies that this day is the same as [date]'s day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 has the same day as 09/03/2018 11:30:30, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDateTime.shouldNotHaveSameDayAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldHaveSameDayAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldHaveSameDayAsFailure]
 */
infix fun LocalDateTime.shouldHaveSameDayAs(date: LocalDateTime) = this should haveSameDay(date)

/**
 * Asserts that this day is NOT the same as [date]'s day
 *
 * Verifies that this year isn't the same as [date]'s day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 doesn't have the same day as 10/02/1998 10:00:00, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDateTime.shouldHaveSameDayAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldNotHaveSameDayAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldNotHaveSameDayAsFailure]
 */
infix fun LocalDateTime.shouldNotHaveSameDayAs(date: LocalDateTime) = this shouldNot haveSameDay(date)

/**
 * Matcher that compares days of LocalDateTimes
 *
 * Verifies that two DateTimes have exactly the same day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 has the same day as 09/03/2018 11:30:30, and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeHaveSameDay]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeHaveSameDayNegation]
 *
 * @see [LocalDateTime.shouldHaveSameDayAs]
 * @see [LocalDateTime.shouldNotHaveSameDayAs]
 */
fun haveSameDay(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

/**
 * Asserts that this day is the same as [date]'s day
 *
 * Verifies that this day is the same as [date]'s day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo has the same day as 09/03/2018 11:30:30 -05:00 America/Chicago,
 * and this assertion should pass for this comparison
 *
 * Opposite of [ZonedDateTime.shouldNotHaveSameDayAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldHaveSameDayAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldHaveSameDayAsFailure]
 */
infix fun ZonedDateTime.shouldHaveSameDayAs(date: ZonedDateTime) = this should haveSameDay(date)

/**
 * Asserts that this day is NOT the same as [date]'s day
 *
 * Verifies that this day isn't the same as [date]'s day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo doesn't have the same day as 10/02/1998 10:00:00 -03:00 America/Sao_Paulo,
 * and this assertion should pass for this comparison
 *
 * Opposite of [ZonedDateTime.shouldHaveSameDayAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldNotHaveSameDayAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldNotHaveSameDayAsFailure]
 */
infix fun ZonedDateTime.shouldNotHaveSameDayAs(date: ZonedDateTime) = this shouldNot haveSameDay(date)

/**
 * Matcher that compares days of ZonedDateTimes
 *
 * Verifies that two ZonedDateTimes have exactly the same day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo has the same day as 09/03/2018 11:30:30 -05:00 America/Chicago,
 * and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeHaveSameDay]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeHaveSameDayNegation]
 *
 * @see [ZonedDateTime.shouldHaveSameDayAs]
 * @see [ZonedDateTime.shouldNotHaveSameDayAs]
 */
fun haveSameDay(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

/**
 * Asserts that this day is the same as [date]'s day
 *
 * Verifies that this day is the same as [date]'s day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 has the day year as 09/02/1998 11:30:30 -05:00,
 * and this assertion should pass for this comparison
 *
 * Opposite of [OffsetDateTime.shouldNotHaveSameDayAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldHaveSameDayAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldHaveSameDayAsFailure]
 */
infix fun OffsetDateTime.shouldHaveSameDayAs(date: OffsetDateTime) = this should haveSameDay(date)

/**
 * Asserts that this day is NOT the same as [date]'s day
 *
 * Verifies that this day isn't the same as [date]'s day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 doesn't have the same day as 10/02/1998 10:00:00 -03:00,
 * and this assertion should pass for this comparison
 *
 * Opposite of [OffsetDateTime.shouldHaveSameDayAs]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldNotHaveSameDayAs]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldNotHaveSameDayAsFailure]
 */
infix fun OffsetDateTime.shouldNotHaveSameDayAs(date: OffsetDateTime) = this shouldNot haveSameDay(date)

/**
 * Matcher that compares days of OffsetDateTimes
 *
 * Verifies that two ZonedDateTimes have exactly the same day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 has the same day as 09/03/2018 11:30:30 -05:00,
 * and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeHaveSameDay]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeHaveSameDayNegation]
 *
 * @see [OffsetDateTime.shouldHaveSameDayAs]
 * @see [OffsetDateTime.shouldNotHaveSameDayAs]
 */
fun haveSameDay(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

/**
 * Asserts that this is before [date]
 *
 * Verifies that this is before [date], comparing year, month and day.
 * For example, 09/02/1998 is before 10/02/1998, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDate.shouldNotBeBefore]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldBeBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldBeBeforeFailure]
 *
 * @see LocalDate.shouldNotBeAfter
 */
infix fun LocalDate.shouldBeBefore(date: LocalDate) = this should before(date)

/**
 * Asserts that this is NOT before [date]
 *
 * Verifies that this is not before [date], comparing year, month and day.
 * For example, 10/02/1998 is not before 09/02/1998, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDate.shouldBeBefore]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldNotBeBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateShouldNotBeBeforeFailure]
 *
 * @see LocalDate.shouldBeAfter
 */
infix fun LocalDate.shouldNotBeBefore(date: LocalDate) = this shouldNot before(date)

/**
 * Matcher that compares two LocalDates and checks whether one is before the other
 *
 * Verifies that two LocalDates occurs in a certain order, checking that one happened before the other.
 * For example, 09/02/1998 is before 10/02/1998, and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateBeforeNegation]
 *
 * @see LocalDate.shouldBeBefore
 * @see LocalDate.shouldNotBeBefore
 */
fun before(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.isBefore(date), "$value should be before $date", "$value should not be before $date")
}

/**
 * Asserts that this is before [date]
 *
 * Verifies that this is before [date], comparing every field in the LocalDateTime.
 * For example, 09/02/1998 00:00:00 is before 09/02/1998 00:00:01, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDateTime.shouldNotBeBefore]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldBeBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldBeBeforeFailure]
 *
 * @see LocalDateTime.shouldNotBeAfter
 */
infix fun LocalDateTime.shouldBeBefore(date: LocalDateTime) = this should before(date)

/**
 * Asserts that this is NOT before [date]
 *
 * Verifies that this is not before [date], comparing every field in the LocalDateTime.
 * For example, 09/02/1998 00:00:01 is not before 09/02/1998 00:00:00, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDateTime.shouldBeBefore]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldNotBeBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeShouldNotBeBeforeFailure]
 *
 * @see LocalDateTime.shouldBeAfter
 */
infix fun LocalDateTime.shouldNotBeBefore(date: LocalDateTime) = this shouldNot before(date)

/**
 * Matcher that compares two LocalDateTimes and checks whether one is before the other
 *
 * Verifies that two LocalDateTimes occurs in a certain order, checking that one happened before the other.
 * For example, 09/02/1998 00:00:00 is before 09/02/1998 00:00:01, and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.localDateTimeBeforeNegation]
 *
 * @see LocalDateTime.shouldBeBefore
 * @see LocalDateTime.shouldNotBeBefore
 */
fun before(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.isBefore(date), "$value should be before $date", "$value should not be before $date")
}

/**
 * Asserts that this is before [date]
 *
 * Verifies that this is before [date], comparing every field in the ZonedDateTime.
 * For example, 09/02/1998 00:00:00 -03:00 America/Sao_Paulo is before 09/02/1998 00:00:01 -03:00 America/Sao_Paulo,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [ZonedDateTime.shouldNotBeBefore]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldBeBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldBeBeforeFailure]
 *
 * @see ZonedDateTime.shouldNotBeAfter
 */
infix fun ZonedDateTime.shouldBeBefore(date: ZonedDateTime) = this should before(date)

/**
 * Asserts that this is NOT before [date]
 *
 * Verifies that this is not before [date], comparing every field in the ZonedDateTime.
 * For example, 09/02/1998 00:00:01 -03:00 America/Sao_Paulo is not before 09/02/1998 00:00:00 -03:00 America/Sao_Paulo,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [ZonedDateTime.shouldBeBefore]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldNotBeBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeShouldNotBeBeforeFailure]
 *
 * @see ZonedDateTime.shouldBeAfter
 */
infix fun ZonedDateTime.shouldNotBeBefore(date: ZonedDateTime) = this shouldNot before(date)

/**
 * Matcher that compares two ZonedDateTimes and checks whether one is before the other
 *
 * Verifies that two ZonedDateTimes occurs in a certain order, checking that one happened before the other.
 * For example, 09/02/1998 00:00:00 -03:00 America/Sao_Paulo is before 09/02/1998 00:00:01 -03:00 America/Sao_Paulo,
 * and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.zonedDateTimeBeforeNegation]
 *
 * @see ZonedDateTime.shouldBeBefore
 * @see ZonedDateTime.shouldNotBeBefore
 */
fun before(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.isBefore(date), "$value should be before $date", "$value should not be before $date")
}

/**
 * Asserts that this is before [date]
 *
 * Verifies that this is before [date], comparing every field in the OffsetDateTime.
 * For example, 09/02/1998 00:00:00 -03:00 is before 09/02/1998 00:00:01 -03:00,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [OffsetDateTime.shouldNotBeBefore]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldBeBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldBeBeforeFailure]
 *
 * @see OffsetDateTime.shouldNotBeAfter
 */
infix fun OffsetDateTime.shouldBeBefore(date: OffsetDateTime) = this should before(date)

/**
 * Asserts that this is NOT before [date]
 *
 * Verifies that this is not before [date], comparing every field in the OffsetDateTime.
 * For example, 09/02/1998 00:00:01 -03:00 is not before 09/02/1998 00:00:00 -03:00,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [OffsetDateTime.shouldBeBefore]
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldNotBeBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeShouldNotBeBeforeFailure]
 *
 * @see OffsetDateTime.shouldBeAfter
 */
infix fun OffsetDateTime.shouldNotBeBefore(date: OffsetDateTime) = this shouldNot before(date)

/**
 * Matcher that compares two OffsetDateTimes and checks whether one is before the other
 *
 * Verifies that two OffsetDateTimes occurs in a certain order, checking that one happened before the other.
 * For example, 09/02/1998 00:00:00 -03:00 is before 09/02/1998 00:00:01 -03:00,
 * and the matcher will have a positive result for this comparison
 *
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeBefore]
 * @sample [com.sksamuel.kotlintest.matchers.date.DateMatchersSamples.offsetDateTimeBeforeNegation]
 *
 * @see OffsetDateTime.shouldBeBefore
 * @see OffsetDateTime.shouldNotBeBefore
 */
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