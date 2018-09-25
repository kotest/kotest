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
 * ```
 *     val firstDate = LocalDate.of(1998, 2, 9)
 *     val secondDate = LocalDate.of(1998, 3, 10)
 *
 *     firstDate shouldHaveSameYearAs secondDate   //Assertion passes
 *
 *
 *     val firstDate = LocalDate.of(2018, 2, 9)
 *     val secondDate = LocalDate.of(1998, 2, 9)
 *
 *     firstDate shouldHaveSameYearAs secondDate   //Assertion fails, 2018 != 1998
 ```
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
 * ```
 *    val firstDate = LocalDate.of(2018, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 9)
 *
 *    firstDate shouldNotHaveSameYearAs secondDate    //Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 3, 10)
 *
 *    firstDate shouldNotHaveSameYearAs  secondDate   //Assertion fails, 1998 == 1998, and we expected a difference
 * ```
 */
infix fun LocalDate.shouldNotHaveSameYearAs(date: LocalDate) = this shouldNot haveSameYear(date)

/**
 * Matcher that compares years of LocalDates
 *
 * Verifies that two dates have exactly the same year, ignoring any other fields.
 * For example, 09/02/1998 has the same year as 10/03/1998, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 3, 10)
 *
 *    firstDate should haveSameYear(secondDate)   //Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(2018, 2, 9)
 *
 *    firstDate shouldNot haveSameYear(secondDate)    //Assertion passes
 * ```
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
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 10, 11, 30, 30)
 *
 *    firstDate shouldHaveSameYearAs secondDate   //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *
 *    firstDate shouldHaveSameYearAs secondDate   //Assertion fails, 2018 != 1998
 * ```
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
 * ```
 *    val firstDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 10, 11, 30, 30)
 *
 *    firstDate shouldNotHaveSameYearAs secondDate    //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 10, 1, 30, 30)
 *
 *    firstDate shouldNotHaveSameYearAs  secondDate   //Assertion fails, 1998 == 1998, and we expected a difference
 * ```
 */
infix fun LocalDateTime.shouldNotHaveSameYearAs(date: LocalDateTime) = this shouldNot haveSameYear(date)

/**
 * Matcher that compares years of LocalDateTimes
 *
 * Verifies that two DateTimes have exactly the same year, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 has the same year as 10/03/1998 11:30:30, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 10, 11, 30, 30)
 *
 *    firstDate should haveSameYear(secondDate)   //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
 *
 *    firstDate shouldNot haveSameYear(secondDate)    //Assertion passes
 * ```
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
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 3, 10, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
 *
 *    firstDate shouldHaveSameYearAs secondDate   //Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldHaveSameYearAs secondDate   //Assertion fails, 2018 != 1998
 * ```
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
 * ```
 *     val firstDate = ZonedDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *     val secondDate = ZonedDateTime.of(1998, 2, 9, 19, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *     firstDate shouldNotHaveSameYearAs secondDate    //Assertion passes
 *
 *
 *     val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *     val secondDate = ZonedDateTime.of(1998, 3, 10, 1, 30, 30, 30, ZoneId.of("America/Chicago"))
 *
 *     firstDate shouldNotHaveSameYearAs  secondDate   //Assertion fails, 1998 == 1998, and we expected a difference
 * ```
 */
infix fun ZonedDateTime.shouldNotHaveSameYearAs(date: ZonedDateTime) = this shouldNot haveSameYear(date)

/**
 * Matcher that compares years of ZonedDateTimes
 *
 * Verifies that two ZonedDateTimes have exactly the same year, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo has the same year as 10/03/1998 11:30:30 -05:00 America/Chicago,
 * and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 3, 10, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
 *
 *    firstDate should haveSameYear(secondDate)   //Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNot haveSameYear(secondDate)    //Assertion passes
 * ```
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
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 3, 10, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate shouldHaveSameYearAs secondDate   //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldHaveSameYearAs secondDate   //Assertion fails, 2018 != 1998
 
 * ```
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
 * ```
 *    val firstDate = OffsetDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 19, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotHaveSameYearAs secondDate    //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 3, 10, 1, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate shouldNotHaveSameYearAs  secondDate   //Assertion fails, 1998 == 1998, and we expected a difference
 * ```
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
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 3, 10, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate should haveSameYear(secondDate)   //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNot haveSameYear(secondDate)    //Assertion passes
 * ```
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
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(2018, 2, 10)
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 3, 9)
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //Assertion fails, 2 != 3
 * ```
 */
infix fun LocalDate.shouldNotHaveSameMonthAs(date: LocalDate) = this shouldNot haveSameMonth(date)

/**
 * Matcher that compares months of LocalDates
 *
 * Verifies that two dates have exactly the same month, ignoring any other fields.
 * For example, 09/02/1998 has the same month as 10/02/2018, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 3, 9)
 *
 *    firstDate shouldNotHaveSameMonthAs secondDate    //Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(2018, 2, 10)
 *
 *    firstDate shouldNotHaveSameMonthAs  secondDate   //Assertion fails, 2 == 2, and we expected a difference
 * ```
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
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(2018, 2, 10)
 *
 *    firstDate should haveSameMonth(secondDate)   //Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 3, 9)
 *
 *    firstDate shouldNot haveSameMonth(secondDate)    //Assertion passes
 * ```
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
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(2018, 2, 10, 11, 30, 30)
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 9, 10, 0, 0)
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //Assertion fails, 2 != 3
 * ```
 */
infix fun LocalDateTime.shouldNotHaveSameMonthAs(date: LocalDateTime) = this shouldNot haveSameMonth(date)

/**
 * Matcher that compares months of LocalDateTimes
 *
 * Verifies that two DateTimes have exactly the same month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 has the same month as 10/02/2018 11:30:30, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 10, 11, 30, 30)
 *
 *    firstDate shouldNotHaveSameMonthAs secondDate    //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 10, 1, 30, 30)
 *
 *    firstDate shouldNotHaveSameMonthAs  secondDate   //Assertion fails, 2 == 2, and we expected a difference
 * ```
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
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(2018, 2, 10, 11, 30, 30)
 *
 *    firstDate should haveSameMonth(secondDate)   //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 9, 10, 0, 0)
 *
 *    firstDate shouldNot haveSameMonth(secondDate)    //Assertion passes
 * ```
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
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2018, 2, 10, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 3, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //Assertion fails, 2 != 3
 * ```
 */
infix fun ZonedDateTime.shouldNotHaveSameMonthAs(date: ZonedDateTime) = this shouldNot haveSameMonth(date)

/**
 * Matcher that compares months of ZonedDateTimes
 *
 * Verifies that two ZonedDateTimes have exactly the same month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo has the same month as 10/02/2018 11:30:30 -05:00 America/Chicago,
 * and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 3, 9, 19, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNotHaveSameMonthAs secondDate    //Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2018, 2, 10, 1, 30, 30, 30, ZoneId.of("America/Chicago"))
 *
 *    firstDate shouldNotHaveSameMonthAs  secondDate   //Assertion fails, 2 == 2, and we expected a difference
 * ```
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
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2018, 2, 10, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 3, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //Assertion fails, 2 != 3
 * ```
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
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 3, 9, 19, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotHaveSameMonthAs secondDate    //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2018, 2, 10, 1, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate shouldNotHaveSameMonthAs  secondDate   //Assertion fails, 2 == 2, and we expected a difference
 * ```
 */
infix fun OffsetDateTime.shouldNotHaveSameMonthAs(date: OffsetDateTime) = this shouldNot haveSameMonth(date)


/**
 * Matcher that compares months of OffsetDateTimes
 *
 * Verifies that two ZonedDateTimes have exactly the same month, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 has the same month as 10/02/1998 11:30:30 -05:00,
 * and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2018, 2, 10, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate should haveSameMonth(secondDate)   //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 3, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNot haveSameMonth(secondDate)    //Assertion passes
 * ```
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
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(2018, 3, 9)
 *
 *    firstDate shouldHaveSameDayAs secondDate   //Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldHaveSameDayAs secondDate   //Assertion fails, 9 != 10
 * ```
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
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldNotHaveSameDayAs secondDate    //Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(2018, 3, 9)
 *
 *    firstDate shouldNotHaveSameDayAs  secondDate   //Assertion fails, 9 == 9, and we expected a difference
 * ```
 */
infix fun LocalDate.shouldNotHaveSameDayAs(date: LocalDate) = this shouldNot haveSameDay(date)

/**
 * Matcher that compares days of LocalDates
 *
 * Verifies that two dates have exactly the same day, ignoring any other fields.
 * For example, 09/02/1998 has the same day as 09/03/2018, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(2018, 3, 9)
 *
 *    firstDate should haveSameDay(secondDate)   //Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldNot haveSameDay(secondDate)    //Assertion passes
 * ```
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
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 9, 11, 30, 30)
 *
 *    firstDate shouldHaveSameDayAs secondDate   //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *
 *    firstDate shouldHaveSameDayAs secondDate   //Assertion fails, 9 != 10
 * ```
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
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *
 *    firstDate shouldNotHaveSameDayAs secondDate    //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 9, 11, 30, 30)
 *
 *    firstDate shouldNotHaveSameDayAs  secondDate   //Assertion fails, 9 == 9, and we expected a difference
 * ```
 */
infix fun LocalDateTime.shouldNotHaveSameDayAs(date: LocalDateTime) = this shouldNot haveSameDay(date)

/**
 * Matcher that compares days of LocalDateTimes
 *
 * Verifies that two DateTimes have exactly the same day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 has the same day as 09/03/2018 11:30:30, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(2018, 3, 9, 11, 30, 30)
 *
 *    firstDate should haveSameDay(secondDate)   //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *
 *    firstDate shouldNot haveSameDay(secondDate)    //Assertion passes
 * ```
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
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2018, 3, 9, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
 *
 *    firstDate shouldHaveSameDayAs secondDate   //Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldHaveSameDayAs secondDate   //Assertion fails, 9 != 10
 * ```
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
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNotHaveSameDayAs secondDate    //Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2018, 3, 9, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
 *
 *    firstDate shouldNotHaveSameDayAs  secondDate   //Assertion fails, 9 == 9, and we expected a difference
 * ```
 */
infix fun ZonedDateTime.shouldNotHaveSameDayAs(date: ZonedDateTime) = this shouldNot haveSameDay(date)

/**
 * Matcher that compares days of ZonedDateTimes
 *
 * Verifies that two ZonedDateTimes have exactly the same day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo has the same day as 09/03/2018 11:30:30 -05:00 America/Chicago,
 * and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2018, 3, 9, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
 *
 *    firstDate should haveSameDay(secondDate)   //Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNot haveSameDay(secondDate)    //Assertion passes
 * ```
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
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2018, 3, 9, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate shouldHaveSameDayAs secondDate   //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldHaveSameDayAs secondDate   //Assertion fails, 9 != 12
 * ```
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
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 10, 19, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotHaveSameDayAs secondDate    //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2018, 3, 9, 1, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate shouldNotHaveSameDayAs  secondDate   //Assertion fails, 9 == 9, and we expected a difference
 * ```
 */
infix fun OffsetDateTime.shouldNotHaveSameDayAs(date: OffsetDateTime) = this shouldNot haveSameDay(date)

/**
 * Matcher that compares days of OffsetDateTimes
 *
 * Verifies that two ZonedDateTimes have exactly the same day, ignoring any other fields.
 * For example, 09/02/1998 10:00:00 -03:00 has the same day as 09/03/2018 11:30:30 -05:00,
 * and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2018, 3, 9, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate should haveSameDay(secondDate)   //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNot haveSameDay(secondDate)    //Assertion passes
 * ```
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
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldBeBefore secondDate    //Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 10)
 *    val secondDate = LocalDate.of(1998, 2, 9)
 *
 *    firstDate shouldBeBefore secondDate     //Assertion fails, 10/02/1998 is not before 09/02/1998 as expected.
 * ```
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
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 10)
 *    val secondDate = LocalDate.of(1998, 2, 9)
 *
 *    firstDate shouldNotBeBefore secondDate    //Assertion passes
 
 
 
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldNotBeBefore secondDate     //Assertion fails, 09/02/1998 is before 10/02/1998, and we expected the opposite.
 * ```
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
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldBe before(secondDate)     //Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 10)
 *    val secondDate = LocalDate.of(1998, 2, 9)
 *
 *    firstDate shouldNotBe before(secondDate)  //Assertion passes
 * ```
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
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 0, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 0, 0, 1)
 *
 *    firstDate shouldBeBefore secondDate    //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 0, 0, 1)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 0, 0, 0)
 *
 *    firstDate shouldBeBefore secondDate     //Assertion fails, firstDate is one second after secondDate
 * ```
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
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 0, 0, 1)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 0, 0, 0)
 *
 *    firstDate shouldNotBeBefore secondDate    //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 0, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 0, 0, 1)
 *
 *    firstDate shouldNotBeBefore secondDate     //Assertion fails, firstDate is one second before secondDate and we didn't expect it
 * ```
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
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 0, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 0, 0, 1)
 *
 *    firstDate shouldBe before(secondDate)     //Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 0, 0, 1)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 0, 0, 0)
 *
 *    firstDate shouldNotBe before(secondDate)  //Assertion passes
 * ```
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
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldBeBefore secondDate    //Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldBeBefore secondDate     //Assertion fails, firstDate is one second after secondDate
 * ```
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
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNotBeBefore secondDate    //Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNotBeBefore secondDate     //Assertion fails, firstDate is one second before secondDate and we didn't expect it
 * ```
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
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldBe before(secondDate)     //Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNotBe before(secondDate)  //Assertion passes
 * ```
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
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldBeBefore secondDate    //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldBeBefore secondDate     //Assertion fails, firstDate is one second after secondDate
 * ```
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
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotBeBefore secondDate    //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotBeBefore secondDate     //Assertion fails, firstDate is one second before secondDate and we didn't expect it
 * ```
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
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldBe before(secondDate)     //Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotBe before(secondDate)  //Assertion passes
 * ```
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