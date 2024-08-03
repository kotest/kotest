package io.kotest.matchers.date

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.TemporalAmount

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
 *     firstDate shouldHaveSameYearAs secondDate   //  Assertion passes
 *
 *
 *     val firstDate = LocalDate.of(2018, 2, 9)
 *     val secondDate = LocalDate.of(1998, 2, 9)
 *
 *     firstDate shouldHaveSameYearAs secondDate   //  Assertion fails, 2018 != 1998
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
 *    firstDate shouldNotHaveSameYearAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 3, 10)
 *
 *    firstDate shouldNotHaveSameYearAs  secondDate   //  Assertion fails, 1998 == 1998, and we expected a difference
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
 *    firstDate should haveSameYear(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(2018, 2, 9)
 *
 *    firstDate shouldNot haveSameYear(secondDate)    //  Assertion passes
 * ```
 *
 * @see [LocalDate.shouldHaveSameYearAs]
 * @see [LocalDate.shouldNotHaveSameYearAs]
 */
fun haveSameYear(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
   override fun test(value: LocalDate): MatcherResult =
      MatcherResult(
         value.year == date.year,
         { "$value should have year ${date.year}" },
         {
            "$value should not have year ${date.year}"
         })
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
 *    firstDate shouldHaveSameYearAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *
 *    firstDate shouldHaveSameYearAs secondDate   //  Assertion fails, 2018 != 1998
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
 *    firstDate shouldNotHaveSameYearAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 10, 1, 30, 30)
 *
 *    firstDate shouldNotHaveSameYearAs  secondDate   //  Assertion fails, 1998 == 1998, and we expected a difference
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
 *    firstDate should haveSameYear(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
 *
 *    firstDate shouldNot haveSameYear(secondDate)    //  Assertion passes
 * ```
 *
 * @see [LocalDateTime.shouldHaveSameYearAs]
 * @see [LocalDateTime.shouldNotHaveSameYearAs]
 */
fun haveSameYear(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult =
      MatcherResult(
         value.year == date.year,
         { "$value should have year ${date.year}" },
         {
            "$value should not have year ${date.year}"
         })
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
 *    firstDate shouldHaveSameYearAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldHaveSameYearAs secondDate   //  Assertion fails, 2018 != 1998
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
 *     firstDate shouldNotHaveSameYearAs secondDate    //  Assertion passes
 *
 *
 *     val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *     val secondDate = ZonedDateTime.of(1998, 3, 10, 1, 30, 30, 30, ZoneId.of("America/Chicago"))
 *
 *     firstDate shouldNotHaveSameYearAs  secondDate   //  Assertion fails, 1998 == 1998, and we expected a difference
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
 *    firstDate should haveSameYear(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNot haveSameYear(secondDate)    //  Assertion passes
 * ```
 *
 * @see [ZonedDateTime.shouldHaveSameYearAs]
 * @see [ZonedDateTime.shouldNotHaveSameYearAs]
 */
fun haveSameYear(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
   override fun test(value: ZonedDateTime): MatcherResult =
      MatcherResult(
         value.year == date.year,
         { "$value should have year ${date.year}" },
         {
            "$value should not have year ${date.year}"
         })
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
 *    firstDate shouldHaveSameYearAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldHaveSameYearAs secondDate   //  Assertion fails, 2018 != 1998

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
 * ```
 *     val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *     val secondDate = OffsetDateTime.of(1999, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))

 *     firstDate shouldNotHaveSameYearAs secondDate    // Assertion passes
 *
 *
 *     val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *     val secondDate = OffsetDateTime.of(1998, 3, 10, 11, 30, 30, 0, ZoneOffset.ofHours(-3))
 *
 *     firstDate shouldNotHaveSameYearAs secondDate    // Assertion fails, 1998 == 1998 and we expected a difference
 *
 * ```
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
 *    firstDate shouldNotHaveSameYearAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 3, 10, 1, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate shouldNotHaveSameYearAs  secondDate   //  Assertion fails, 1998 == 1998, and we expected a difference
 * ```
 *
 * @see [OffsetDateTime.shouldHaveSameYearAs]
 * @see [OffsetDateTime.shouldNotHaveSameYearAs]
 */
fun haveSameYear(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
   override fun test(value: OffsetDateTime): MatcherResult =
      MatcherResult(
         value.year == date.year,
         { "$value should have year ${date.year}" },
         {
            "$value should not have year ${date.year}"
         })
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
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 3, 10)
 *
 *    firstDate should haveSameYear(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(2018, 2, 9)
 *
 *    firstDate shouldNot haveSameYear(secondDate)    //  Assertion passes
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
 *    firstDate shouldHaveSameMonthAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 3, 9)
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //  Assertion fails, 2 != 3
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
 *    firstDate shouldNotHaveSameMonthAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(2018, 2, 10)
 *
 *    firstDate shouldNotHaveSameMonthAs  secondDate   //  Assertion fails, 2 == 2, and we expected a difference
 * ```
 *
 * @see [LocalDate.shouldHaveSameMonthAs]
 * @see [LocalDate.shouldNotHaveSameMonthAs]
 */
fun haveSameMonth(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
   override fun test(value: LocalDate): MatcherResult =
      MatcherResult(
         value.month == date.month,
         { "$value should have month ${date.month}" },
         {
            "$value should not have month ${date.month}"
         })
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
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(2018, 2, 10, 10, 0, 0)
 *
 *    firstDate should haveSameMonth(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 9, 10, 0, 0)
 *
 *    firstDate shouldNot haveSameMonth(secondDate)    //  Assertion passes
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
 *    firstDate shouldHaveSameMonthAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 9, 10, 0, 0)
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //  Assertion fails, 2 != 3
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
 *    firstDate shouldNotHaveSameMonthAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 10, 1, 30, 30)
 *
 *    firstDate shouldNotHaveSameMonthAs  secondDate   //  Assertion fails, 2 == 2, and we expected a difference
 * ```
 *
 * @see [LocalDateTime.shouldHaveSameMonthAs]
 * @see [LocalDateTime.shouldNotHaveSameMonthAs]
 */
fun haveSameMonth(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult =
      MatcherResult(
         value.month == date.month,
         { "$value should have month ${date.month}" },
         {
            "$value should not have month ${date.month}"
         })
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
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2018, 2, 10, 11, 30, 30, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate should haveSameMonth(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 3, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNot haveSameMonth(secondDate)    //  Assertion passes
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
 *    firstDate shouldHaveSameMonthAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 3, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //  Assertion fails, 2 != 3
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
 *    firstDate shouldNotHaveSameMonthAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2018, 2, 10, 1, 30, 30, 30, ZoneId.of("America/Chicago"))
 *
 *    firstDate shouldNotHaveSameMonthAs  secondDate   //  Assertion fails, 2 == 2, and we expected a difference
 * ```
 *
 * @see [ZonedDateTime.shouldHaveSameMonthAs]
 * @see [ZonedDateTime.shouldNotHaveSameMonthAs]
 */
fun haveSameMonth(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
   override fun test(value: ZonedDateTime): MatcherResult =
      MatcherResult(
         value.month == date.month,
         { "$value should have month ${date.month}" },
         {
            "$value should not have month ${date.month}"
         })
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
 *    firstDate shouldHaveSameMonthAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 3, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //  Assertion fails, 2 != 3
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
 *    firstDate shouldNotHaveSameMonthAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2018, 2, 10, 1, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate shouldNotHaveSameMonthAs  secondDate   //  Assertion fails, 2 == 2, and we expected a difference
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
 *    firstDate should haveSameMonth(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 3, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNot haveSameMonth(secondDate)    //  Assertion passes
 * ```
 *
 * @see [OffsetDateTime.shouldHaveSameMonthAs]
 * @see [OffsetDateTime.shouldNotHaveSameMonthAs]
 */
fun haveSameMonth(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
   override fun test(value: OffsetDateTime): MatcherResult =
      MatcherResult(
         value.month == date.month,
         { "$value should have month ${date.month}" },
         {
            "$value should not have month ${date.month}"
         })
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
 *    firstDate shouldHaveSameDayAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldHaveSameDayAs secondDate   //  Assertion fails, 9 != 10
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
 *    firstDate shouldNotHaveSameDayAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(2018, 3, 9)
 *
 *    firstDate shouldNotHaveSameDayAs  secondDate   //  Assertion fails, 9 == 9, and we expected a difference
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
 *    firstDate should haveSameDay(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldNot haveSameDay(secondDate)    //  Assertion passes
 * ```
 *
 * @see [LocalDate.shouldHaveSameDayAs]
 * @see [LocalDate.shouldNotHaveSameDayAs]
 */
fun haveSameDay(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
   override fun test(value: LocalDate): MatcherResult =
      MatcherResult(
         value.dayOfMonth == date.dayOfMonth,
         { "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}" },
         {
            "$value should not have day ${date.dayOfMonth}"
         })
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
 *    firstDate shouldHaveSameDayAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *
 *    firstDate shouldHaveSameDayAs secondDate   //  Assertion fails, 9 != 10
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
 *    firstDate shouldNotHaveSameDayAs secondDate    // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 3, 9, 11, 30, 30)
 *
 *    firstDate shouldNotHaveSameDayAs  secondDate   // Assertion fails, 9 == 9, and we expected a difference
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
 *    firstDate should haveSameDay(secondDate)   // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *
 *    firstDate shouldNot haveSameDay(secondDate)    // Assertion passes
 * ```
 *
 * @see [LocalDateTime.shouldHaveSameDayAs]
 * @see [LocalDateTime.shouldNotHaveSameDayAs]
 */
fun haveSameDay(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult =
      MatcherResult(
         value.dayOfMonth == date.dayOfMonth,
         { "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}" },
         {
            "$value should not have day ${date.dayOfMonth}"
         })
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
 *    firstDate shouldHaveSameDayAs secondDate   // Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldHaveSameDayAs secondDate   // Assertion fails, 9 != 10
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
 *    firstDate shouldNotHaveSameDayAs secondDate    // Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2018, 3, 9, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
 *
 *    firstDate shouldNotHaveSameDayAs  secondDate   // Assertion fails, 9 == 9, and we expected a difference
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
 *    firstDate should haveSameDay(secondDate)   // Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNot haveSameDay(secondDate)    // Assertion passes
 * ```
 *
 * @see [ZonedDateTime.shouldHaveSameDayAs]
 * @see [ZonedDateTime.shouldNotHaveSameDayAs]
 */
fun haveSameDay(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
   override fun test(value: ZonedDateTime): MatcherResult =
      MatcherResult(
         value.dayOfMonth == date.dayOfMonth,
         { "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}" },
         {
            "$value should not have day ${date.dayOfMonth}"
         })
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
 *    firstDate shouldHaveSameDayAs secondDate   // Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldHaveSameDayAs secondDate   // Assertion fails, 9 != 12
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
 *    firstDate shouldNotHaveSameDayAs secondDate    // Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2018, 3, 9, 1, 30, 30, 30, ZoneOffset.ofHours(-5))
 *
 *    firstDate shouldNotHaveSameDayAs  secondDate   // Assertion fails, 9 == 9, and we expected a difference
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
 *    firstDate should haveSameDay(secondDate)   // Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNot haveSameDay(secondDate)    // Assertion passes
 * ```
 *
 * @see [OffsetDateTime.shouldHaveSameDayAs]
 * @see [OffsetDateTime.shouldNotHaveSameDayAs]
 */
fun haveSameDay(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
   override fun test(value: OffsetDateTime): MatcherResult =
      MatcherResult(
         value.dayOfMonth == date.dayOfMonth,
         { "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}" },
         {
            "$value should not have day ${date.dayOfMonth}"
         })
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
 *    firstDate shouldBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 10)
 *    val secondDate = LocalDate.of(1998, 2, 9)
 *
 *    firstDate shouldBeBefore secondDate     // Assertion fails, 10/02/1998 is not before 09/02/1998 as expected.
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
 *    firstDate shouldNotBeBefore secondDate    // Assertion passes



 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldNotBeBefore secondDate     // Assertion fails, 09/02/1998 is before 10/02/1998, and we expected the opposite.
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
 *    firstDate shouldBe before(secondDate)     // Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 10)
 *    val secondDate = LocalDate.of(1998, 2, 9)
 *
 *    firstDate shouldNotBe before(secondDate)  // Assertion passes
 * ```
 *
 * @see LocalDate.shouldBeBefore
 * @see LocalDate.shouldNotBeBefore
 */
fun before(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
   override fun test(value: LocalDate): MatcherResult =
      MatcherResult(
         value.isBefore(date),
         { "$value should be before $date" },
         { "$value should not be before $date" })
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
 *    firstDate shouldBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 0, 0, 1)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 0, 0, 0)
 *
 *    firstDate shouldBeBefore secondDate     // Assertion fails, firstDate is one second after secondDate
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
 *    firstDate shouldNotBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 0, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 0, 0, 1)
 *
 *    firstDate shouldNotBeBefore secondDate     // Assertion fails, firstDate is one second before secondDate and we didn't expect it
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
 *    firstDate shouldBe before(secondDate)     // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 0, 0, 1)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 0, 0, 0)
 *
 *    firstDate shouldNotBe before(secondDate)  // Assertion passes
 * ```
 *
 * @see LocalDateTime.shouldBeBefore
 * @see LocalDateTime.shouldNotBeBefore
 */
fun before(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult =
      MatcherResult(
         value.isBefore(date),
         { "$value should be before $date" },
         { "$value should not be before $date" })
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
 *    firstDate shouldBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldBeBefore secondDate     // Assertion fails, firstDate is one second after secondDate
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
 *    firstDate shouldNotBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNotBeBefore secondDate     // Assertion fails, firstDate is one second before secondDate and we didn't expect it
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
 *    firstDate shouldBe before(secondDate)     // Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNotBe before(secondDate)  // Assertion passes
 * ```
 *
 * @see ZonedDateTime.shouldBeBefore
 * @see ZonedDateTime.shouldNotBeBefore
 */
fun before(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
   override fun test(value: ZonedDateTime): MatcherResult =
      MatcherResult(
         value.isBefore(date),
         { "$value should be before $date" },
         { "$value should not be before $date" })
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
 *    firstDate shouldBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldBeBefore secondDate     // Assertion fails, firstDate is one second after secondDate
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
 *    firstDate shouldNotBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotBeBefore secondDate     // Assertion fails, firstDate is one second before secondDate and we didn't expect it
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
 *    firstDate shouldBe before(secondDate)     // Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotBe before(secondDate)  // Assertion passes
 * ```
 *
 * @see OffsetDateTime.shouldBeBefore
 * @see OffsetDateTime.shouldNotBeBefore
 */
fun before(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
   override fun test(value: OffsetDateTime): MatcherResult =
      MatcherResult(
         value.isBefore(date),
         { "$value should be before $date" },
         { "$value should not be before $date" })
}

/**
 * Asserts that this is after [date]
 *
 * Verifies that this is after [date], comparing year, month and day.
 * For example, 09/02/1998 is after 08/02/1998, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDate.shouldNotBeAfter]
 *
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 8)
 *
 *    firstDate shouldBeAfter secondDate  // Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldBeAfter secondDate  // Assertion fails, firstDate is NOT after secondDate
 * ```
 *
 * @see LocalDate.shouldNotBeBefore
 */
infix fun LocalDate.shouldBeAfter(date: LocalDate) = this should after(date)

/**
 * Asserts that this is NOT after [date]
 *
 * Verifies that this is not after [date], comparing year, month and day.
 * For example, 09/02/1998 is not after 10/02/1998, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDate.shouldBeAfter]
 *
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldNotBeAfter secondDate   // Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 10)
 *    val secondDate = LocalDate.of(1998, 2, 9)
 *
 *    firstDate shouldNotBeAfter secondDate   // Assertion fails, first date IS after secondDate
 * ```
 *
 * @see LocalDate.shouldBeBefore
 */
infix fun LocalDate.shouldNotBeAfter(date: LocalDate) = this shouldNot after(date)

/**
 * Matcher that compares two LocalDates and checks whether one is after the other
 *
 * Verifies that two LocalDates occurs in a certain order, checking that one happened after the other.
 * For example, 10/02/1998 is after 09/02/1998, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 8)
 *
 *    firstDate shouldBe after(secondDate ) // Assertion passes
 *
 *
 *    val firstDate = LocalDate.of(1998, 2, 9)
 *    val secondDate = LocalDate.of(1998, 2, 10)
 *
 *    firstDate shouldNotBe after(secondDate)   // Assertion passes
 * ```
 *
 * @see LocalDate.shouldBeAfter
 * @see LocalDate.shouldNotBeAfter
 */
fun after(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
   override fun test(value: LocalDate): MatcherResult =
      MatcherResult(
         value.isAfter(date),
         { "$value should be after $date" },
         { "$value should not be after $date" })
}

/**
 * Asserts that this is after [date]
 *
 * Verifies that this is after [date], comparing all fields in LocalDateTime.
 * For example, 09/02/1998 10:00:00 is after 09/02/1998 09:00:00, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDateTime.shouldNotBeAfter]
 *
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 8, 10, 0, 0)
 *
 *    firstDate shouldBeAfter secondDate  // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *
 *    firstDate shouldBeAfter secondDate  // Assertion fails, firstDate is NOT after secondDate
 * ```
 *
 * @see LocalDateTime.shouldNotBeBefore
 */
infix fun LocalDateTime.shouldBeAfter(date: LocalDateTime) = this should after(date)

/**
 * Asserts that this is NOT after [date]
 *
 * Verifies that this is not after [date], comparing all fields in LocalDateTime.
 * For example, 09/02/1998 09:00:00 is not after 09/02/1998 10:00:00, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDateTime.shouldBeAfter]
 *
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *
 *    firstDate shouldNotBeAfter secondDate   // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *
 *    firstDate shouldNotBeAfter secondDate   // Assertion fails, first date IS after secondDate
 * ```
 *
 * @see LocalDateTime.shouldBeBefore
 */
infix fun LocalDateTime.shouldNotBeAfter(date: LocalDateTime) = this shouldNot after(date)

/**
 * Matcher that compares two LocalDateTimes and checks whether one is after the other
 *
 * Verifies that two LocalDateTimes occurs in a certain order, checking that one happened after the other.
 * For example, 09/02/1998 10:00:00 is after 09/02/1998 09:00:00, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 8, 10, 0, 0)
 *
 *    firstDate shouldBe after(secondDate ) // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *
 *    firstDate shouldNotBe after(secondDate)   // Assertion passes
 * ```
 *
 * @see LocalDateTime.shouldBeAfter
 * @see LocalDateTime.shouldNotBeAfter
 */
fun after(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult =
      MatcherResult(
         value.isAfter(date),
         { "$value should be after $date" },
         { "$value should not be after $date" })
}

/**
 * Asserts that this is after [date]
 *
 * Verifies that this is after [date], comparing all fields in ZonedDateTime.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo is after 09/02/1998 09:00:00 -03:00 America/Sao_Paulo,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [ZonedDateTime.shouldNotBeAfter]
 *
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 8, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldBeAfter secondDate  // Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldBeAfter secondDate  // Assertion fails, firstDate is NOT after secondDate
 * ```
 *
 * @see ZonedDateTime.shouldNotBeBefore
 */
infix fun ZonedDateTime.shouldBeAfter(date: ZonedDateTime) = this should after(date)

/**
 * Asserts that this is NOT after [date]
 *
 * Verifies that this is not after [date], comparing all fields in ZonedDateTime.
 * For example, 09/02/1998 09:00:00 -03:00 America/Sao_Paulo is not after 09/02/1998 10:00:00 -03:00 America/Sao_Paulo,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [ZonedDateTime.shouldBeAfter]
 *
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNotBeAfter secondDate   // Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNotBeAfter secondDate   // Assertion fails, first date IS after secondDate
 * ```
 *
 * @see ZonedDateTime.shouldBeBefore
 */
infix fun ZonedDateTime.shouldNotBeAfter(date: ZonedDateTime) = this shouldNot after(date)

/**
 * Matcher that compares two ZonedDateTimes and checks whether one is after the other
 *
 * Verifies that two ZonedDateTimes occurs in a certain order, checking that one happened after the other.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo is after 09/02/1998 09:00:00 -03:00 America/Sao_Paulo,
 * and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 8, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldBe after(secondDate ) // Assertion passes
 *
 *
 *    val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    firstDate shouldNotBe after(secondDate)   // Assertion passes
 * ```
 *
 * @see ZonedDateTime.shouldBeAfter
 * @see ZonedDateTime.shouldNotBeAfter
 */
fun after(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
   override fun test(value: ZonedDateTime): MatcherResult =
      MatcherResult(
         value.isAfter(date),
         { "$value should be after $date" },
         { "$value should not be after $date" })
}

/**
 * Asserts that this is after [date]
 *
 * Verifies that this is after [date], comparing all fields in OffsetDateTime.
 * For example, 09/02/1998 10:00:00 -03:00 is after 09/02/1998 09:00:00 -03:00,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [OffsetDateTime.shouldNotBeAfter]
 *
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 8, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldBeAfter secondDate  // Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldBeAfter secondDate  // Assertion fails, firstDate is NOT after secondDate
 * ```
 *
 * @see OffsetDateTime.shouldNotBeBefore
 */
infix fun OffsetDateTime.shouldBeAfter(date: OffsetDateTime) = this should after(date)

/**
 * Asserts that this is NOT after [date]
 *
 * Verifies that this is not after [date], comparing all fields in OffsetDateTime.
 * For example, 09/02/1998 09:00:00 -03:00 is not after 09/02/1998 10:00:00 -03:00,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [OffsetDateTime.shouldBeAfter]
 *
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotBeAfter secondDate   // Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotBeAfter secondDate   // Assertion fails, first date IS after secondDate
 * ```
 *
 * @see OffsetDateTime.shouldBeBefore
 */
infix fun OffsetDateTime.shouldNotBeAfter(date: OffsetDateTime) = this shouldNot after(date)

/**
 * Matcher that compares two OffsetDateTimes and checks whether one is after the other
 *
 * Verifies that two OffsetDateTimes occurs in a certain order, checking that one happened after the other.
 * For example, 09/02/1998 10:00:00 -03:00 is after 09/02/1998 09:00:00 -03:00,
 * and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 8, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldBe after(secondDate ) // Assertion passes
 *
 *
 *    val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotBe after(secondDate)   // Assertion passes
 * ```
 *
 * @see OffsetDateTime.shouldBeAfter
 * @see OffsetDateTime.shouldNotBeAfter
 */
fun after(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
   override fun test(value: OffsetDateTime): MatcherResult =
      MatcherResult(
         value.isAfter(date),
         { "$value should be after $date" },
         { "$value should not be after $date" })
}

/**
 * Asserts that this is within [period] of [date]
 *
 * Verifies that this is within [period] of [date].
 * For example, 09/02/1998 is within 3 days of 10/02/1998, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDate.shouldNotBeWithin]
 *
 * ```
 *     val firstDate = LocalDate.of(1998, 2, 9)
 *     val secondDate = LocalDate.of(1998, 2, 10)
 *
 *     firstDate.shouldBeWithin(Period.ofDays(3), secondDate) // Assertion passes
 *
 *
 *     val firstDate = LocalDate.of(1998, 2, 9)
 *     val secondDate = LocalDate.of(1998, 2, 25)
 *
 *     firstDate.shouldBeWithin(Period.ofDays(3), secondDate)   // Assertion fails, firstDate is not within 3 days of secondDate
 * ```
 */
fun LocalDate.shouldBeWithin(period: Period, date: LocalDate) = this should within(period, date)

/**
 * Asserts that this is NOT within [period] of [date]
 *
 * Verifies that this is not within [period] of [date].
 * For example, 09/02/1998 is not within 3 days of 25/02/1998, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDate.shouldBeWithin]
 *
 * ```
 *     val firstDate = LocalDate.of(1998, 2, 9)
 *     val secondDate = LocalDate.of(1998, 2, 25)
 *
 *     firstDate.shouldNotBeWithin(Period.ofDays(3), secondDate) // Assertion passes
 *
 *
 *     val firstDate = LocalDate.of(1998, 2, 9)
 *     val secondDate = LocalDate.of(1998, 2, 10)
 *
 *     firstDate.shouldNotBeWithin(Period.ofDays(3), secondDate)   // Assertion fails, firstDate is within 3 days of secondDate, and we expected not to
 * ```
 */
fun LocalDate.shouldNotBeWithin(period: Period, date: LocalDate) = this shouldNot within(period, date)

/**
 * Matcher that compares two LocalDates and checks whether one is within [period] of the other
 *
 * Verifies that two LocalDates are within a certain period.
 * For example, 09/02/1998 is within 3 days of 10/02/1998, and the matcher will have a positive result for this comparison.
 *
 *
 * ```
 *     val firstDate = LocalDate.of(1998, 2, 9)
 *     val secondDate = LocalDate.of(1998, 2, 10)
 *
 *     firstDate shouldBe within(Period.ofDays(3), secondDate)    // Assertion passes
 *
 *
 *     val firstDate = LocalDate.of(1998, 2, 9)
 *     val secondDate = LocalDate.of(1998, 2, 25)
 *     firstDate shouldNotBe within(Period.ofDays(3), secondDate)     // Assertion passes
 * ```
 *
 * @see [LocalDate.shouldBeWithin]
 * @see [LocalDate.shouldNotBeWithin]
 */
fun within(period: Period, date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
   override fun test(value: LocalDate): MatcherResult {
      val start = date.minus(period)
      val end = date.plus(period)
      val passed = start == value || end == value || start.isBefore(value) && end.isAfter(value)
      return MatcherResult(
         passed,
         { "$value should be within $period of $date" },
         {
            "$value should not be within $period of $date"
         })
   }
}

/**
 * Asserts that this is within [temporalAmount] of [date]
 *
 * Verifies that this is within [temporalAmount] of [date].
 * For example, 09/02/1998 10:00:00 is within 3 days of 10/02/1998 10:00:00, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDateTime.shouldNotBeWithin]
 *
 * ```
 *     val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *     val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *
 *     firstDate.shouldBeWithin(Period.ofDays(3), secondDate) // Assertion passes
 *
 *
 *     val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *     val secondDate = LocalDateTime.of(1998, 2, 25, 10, 0, 0)
 *
 *     firstDate.shouldBeWithin(Period.ofDays(3), secondDate)   // Assertion fails, firstDate is not within 3 days of secondDate
 * ```
 */
fun LocalDateTime.shouldBeWithin(temporalAmount: TemporalAmount, date: LocalDateTime) = this should within(temporalAmount, date)

/**
 * Asserts that this is NOT within [temporalAmount] of [date]
 *
 * Verifies that this is not within [temporalAmount] of [date].
 * For example, 09/02/1998 10:00:00 is not within 3 days of 25/02/1998 10:00:00, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalDateTime.shouldBeWithin]
 *
 * ```
 *     val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *     val secondDate = LocalDateTime.of(1998, 2, 25, 10, 0, 0)
 *
 *     firstDate.shouldNotBeWithin(Period.ofDays(3), secondDate) // Assertion passes
 *
 *
 *     val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *     val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *
 *     firstDate.shouldNotBeWithin(Period.ofDays(3), secondDate)   // Assertion fails, firstDate is within 3 days of secondDate, and we expected not to
 * ```
 */
fun LocalDateTime.shouldNotBeWithin(temporalAmount: TemporalAmount, date: LocalDateTime) = this shouldNot within(temporalAmount, date)

/**
 * Matcher that compares two LocalDateTimes and checks whether one is within [temporalAmount] of the other
 *
 * Verifies that two LocalDateTimes are within a certain period.
 * For example, 09/02/1998 10:00:00 is within 3 days of 10/02/1998 10:00:00,
 * and the matcher will have a positive result for this comparison.
 *
 *
 * ```
 *     val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *     val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
 *
 *     firstDate shouldBe within(Period.ofDays(3), secondDate)    // Assertion passes
 *
 *
 *     val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
 *     val secondDate = LocalDateTime.of(1998, 2, 25, 10, 0, 0)
 *     firstDate shouldNotBe within(Period.ofDays(3), secondDate)     // Assertion passes
 * ```
 *
 * @see [LocalDateTime.shouldBeWithin]
 * @see [LocalDateTime.shouldNotBeWithin]
 */
fun within(temporalAmount: TemporalAmount, date: LocalDateTime): Matcher<LocalDateTime> = object :
  Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult {
      val start = date.minus(temporalAmount)
      val end = date.plus(temporalAmount)
      val passed = start == value || end == value || start.isBefore(value) && end.isAfter(value)
      return MatcherResult(
         passed,
         { "$value should be within $temporalAmount of $date" },
         {
            "$value should not be within $temporalAmount of $date"
         })
   }
}

/**
 * Asserts that this is within [temporalAmount] of [date]
 *
 * Verifies that this is within [temporalAmount] of [date].
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo is within 3 days of 10/02/1998 10:00:00 -03:00 America/Sao_Paulo,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [ZonedDateTime.shouldNotBeWithin]
 *
 * ```
 *     val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *     val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *
 *     firstDate.shouldBeWithin(Period.ofDays(3), secondDate) // Assertion passes
 *
 *
 *     val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *     val secondDate = ZonedDateTime.of(1998, 2, 25, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *
 *     firstDate.shouldBeWithin(Period.ofDays(3), secondDate)   // Assertion fails, firstDate is not within 3 days of secondDate
 * ```
 */
fun ZonedDateTime.shouldBeWithin(temporalAmount: TemporalAmount, date: ZonedDateTime) = this should within(temporalAmount, date)

/**
 * Asserts that this is NOT within [temporalAmount] of [date]
 *
 * Verifies that this is not within [temporalAmount] of [date].
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo is not within 3 days of 25/02/1998 10:00:00 -03:00 America/Sao_Paulo,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [ZonedDateTime.shouldBeWithin]
 *
 * ```
 *     val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *     val secondDate = ZonedDateTime.of(1998, 2, 25, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *
 *     firstDate.shouldNotBeWithin(Period.ofDays(3), secondDate) // Assertion passes
 *
 *
 *     val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *     val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *
 *     firstDate.shouldNotBeWithin(Period.ofDays(3), secondDate)   // Assertion fails, firstDate is within 3 days of secondDate, and we expected not to
 * ```
 */
fun ZonedDateTime.shouldNotBeWithin(temporalAmount: TemporalAmount, date: ZonedDateTime) = this shouldNot within(temporalAmount, date)

/**
 * Matcher that compares two ZonedDateTimes and checks whether one is within [temporalAmount] of the other
 *
 * Verifies that two ZonedDateTimes are within a certain period.
 * For example, 09/02/1998 10:00:00 -03:00 America/Sao_Paulo is within 3 days of 10/02/1998 10:00:00 -03:00 America/Sao_Paulo,
 * and the matcher will have a positive result for this comparison.
 *
 *
 * ```
 *     val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *     val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *
 *     firstDate shouldBe within(Period.ofDays(3), secondDate)    // Assertion passes
 *
 *
 *     val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *     val secondDate = ZonedDateTime.of(1998, 2, 25, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo))
 *     firstDate shouldNotBe within(Period.ofDays(3), secondDate)     // Assertion passes
 * ```
 *
 * @see [ZonedDateTime.shouldBeWithin]
 * @see [ZonedDateTime.shouldNotBeWithin]
 */
fun within(temporalAmount: TemporalAmount, date: ZonedDateTime): Matcher<ZonedDateTime> = object :
  Matcher<ZonedDateTime> {
   override fun test(value: ZonedDateTime): MatcherResult {
      val start = date.minus(temporalAmount)
      val end = date.plus(temporalAmount)
      val passed = start == value || end == value || start.isBefore(value) && end.isAfter(value)
      return MatcherResult(
         passed,
         { "$value should be within $temporalAmount of $date" },
         {
            "$value should not be within $temporalAmount of $date"
         })
   }
}

/**
 * Asserts that this is within [temporalAmount] of [date]
 *
 * Verifies that this is within [temporalAmount] of [date].
 * For example, 09/02/1998 10:00:00 -03:00 is within 3 days of 10/02/1998 10:00:00 -03:00,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [OffsetDateTime.shouldNotBeWithin]
 *
 * ```
 *     val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3)
 *     val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *     firstDate.shouldBeWithin(Period.ofDays(3), secondDate) // Assertion passes
 *
 *
 *     val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *     val secondDate = OffsetDateTime.of(1998, 2, 25, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *     firstDate.shouldBeWithin(Period.ofDays(3), secondDate)   // Assertion fails, firstDate is not within 3 days of secondDate
 * ```
 */
fun OffsetDateTime.shouldBeWithin(temporalAmount: TemporalAmount, date: OffsetDateTime) = this should within(temporalAmount, date)

/**
 * Asserts that this is NOT within [temporalAmount] of [date]
 *
 * Verifies that this is not within [temporalAmount] of [date].
 * For example, 09/02/1998 10:00:00 -03:00 is not within 3 days of 25/02/1998 10:00:00 -03:00,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [OffsetDateTime.shouldBeWithin]
 *
 * ```
 *     val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *     val secondDate = OffsetDateTime.of(1998, 2, 25, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *     firstDate.shouldNotBeWithin(Period.ofDays(3), secondDate) // Assertion passes
 *
 *
 *     val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *     val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *     firstDate.shouldNotBeWithin(Period.ofDays(3), secondDate)   // Assertion fails, firstDate is within 3 days of secondDate, and we expected not to
 * ```
 */
fun OffsetDateTime.shouldNotBeWithin(temporalAmount: TemporalAmount, date: OffsetDateTime) = this shouldNot within(temporalAmount, date)

/**
 * Matcher that compares two OffsetDateTimes and checks whether one is within [temporalAmount] of the other
 *
 * Verifies that two OffsetDateTimes are within a certain period.
 * For example, 09/02/1998 10:00:00 -03:00 is within 3 days of 10/02/1998 10:00:00 -03:00,
 * and the matcher will have a positive result for this comparison.
 *
 *
 * ```
 *     val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *     val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *     firstDate shouldBe within(Period.ofDays(3), secondDate)    // Assertion passes
 *
 *
 *     val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *     val secondDate = OffsetDateTime.of(1998, 2, 25, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
 *     firstDate shouldNotBe within(Period.ofDays(3), secondDate)     // Assertion passes
 * ```
 *
 * @see [OffsetDateTime.shouldBeWithin]
 * @see [OffsetDateTime.shouldNotBeWithin]
 */
fun within(temporalAmount: TemporalAmount, date: OffsetDateTime): Matcher<OffsetDateTime> = object :
  Matcher<OffsetDateTime> {
   override fun test(value: OffsetDateTime): MatcherResult {
      val start = date.minus(temporalAmount)
      val end = date.plus(temporalAmount)
      val passed = start == value || end == value || start.isBefore(value) && end.isAfter(value)
      return MatcherResult(
         passed,
         { "$value should be within $temporalAmount of $date" },
         {
            "$value should not be within $temporalAmount of $date"
         })
   }
}

/**
 * Asserts that this is between [a] and [b]
 *
 * Verifies that this is after [a] and before [b], comparing year, month and day.
 *
 * Opposite of [LocalDate.shouldNotBeBetween]
 *
 * ```
 *    val date = LocalDate.of(2019, 2, 16)
 *    val firstDate = LocalDate.of(2019, 2, 15)
 *    val secondDate = LocalDate.of(2019, 2, 17)
 *
 *    date.shouldBeBetween(firstDate, secondDate)  // Assertion passes
 *
 *
 *    val date = LocalDate.of(2019, 2, 15)
 *    val firstDate = LocalDate.of(2019, 2, 16)
 *    val secondDate = LocalDate.of(2019, 2, 17)
 *
 *    date.shouldBeBetween(firstDate, secondDate)  // Assertion fails, date is NOT between firstDate and secondDate
 * ```
 *
 * @see LocalDate.shouldNotBeBetween
 */
fun LocalDate.shouldBeBetween(a: LocalDate, b: LocalDate) = this shouldBe between(a, b)

/**
 * Asserts that this is NOT between [a] and [b]
 *
 * Verifies that this is not after [a] and before [b], comparing year, month and day.
 *
 * Opposite of [LocalDate.shouldBeBetween]
 *
 * ```
 *    val date = LocalDate.of(2019, 2, 15)
 *    val firstDate = LocalDate.of(2019, 2, 16)
 *    val secondDate = LocalDate.of(2019, 2, 17)
 *
 *    date.shouldNotBeBetween(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = LocalDate.of(2019, 2, 16)
 *    val firstDate = LocalDate.of(2019, 2, 15)
 *    val secondDate = LocalDate.of(2019, 2, 17)
 *
 *    date.shouldNotBeBetween(firstDate, secondDate)  // Assertion fails, date IS between firstDate and secondDate
 * ```
 *
 * @see LocalDate.shouldBeBetween
 */
fun LocalDate.shouldNotBeBetween(a: LocalDate, b: LocalDate) = this shouldNotBe between(a, b)

/**
 * Matcher that checks if LocalDate is between two other LocalDates
 *
 * Verifies that LocalDate is after the first LocalDate and before the second LocalDate
 * For example, 20/03/2019 is between 19/03/2019 and 21/03/2019, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val date = LocalDate.of(2019, 2, 16)
 *    val firstDate = LocalDate.of(2019, 2, 15)
 *    val secondDate = LocalDate.of(2019, 2, 17)
 *
 *    date shouldBe after(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = LocalDate.of(2019, 2, 15)
 *    val firstDate = LocalDate.of(2019, 2, 16)
 *    val secondDate = LocalDate.of(2019, 2, 17)
 *
 *    date shouldNotBe between(firstDate, secondDate)   // Assertion passes
 * ```
 *
 * @see LocalDate.shouldBeBetween
 * @see LocalDate.shouldNotBeBetween
 */
fun between(a: LocalDate, b: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
   override fun test(value: LocalDate): MatcherResult {
      val passed = value.isAfter(a) && value.isBefore(b)
      return MatcherResult(
         passed,
         { "$value should be after $a and before $b" },
         {
            "$value should not be be after $a and before $b"
         })
   }
}

/**
 * Asserts that this is between [a] and [b]
 *
 * Verifies that this is after [a] and before [b], comparing all fields in LocalDateTime.
 *
 * Opposite of [LocalDateTime.shouldNotBeBetween]
 *
 * ```
 *    val date = LocalDateTime.of(2019, 2, 16, 12, 0, 0)
 *    val firstDate = LocalDateTime.of(2019, 2, 15, 12, 0, 0)
 *    val secondDate = LocalDateTime.of(2019, 2, 17, 12, 0, 0)
 *
 *    date.shouldBeBetween(firstDate, secondDate)  // Assertion passes
 *
 *
 *    val date = LocalDateTime.of(2019, 2, 15, 12, 0, 0)
 *    val firstDate = LocalDateTime.of(2019, 2, 16, 12, 0, 0)
 *    val secondDate = LocalDateTime.of(2019, 2, 17, 12, 0, 0)
 *
 *    date.shouldBeBetween(firstDate, secondDate)  // Assertion fails, date is NOT between firstDate and secondDate
 * ```
 *
 * @see LocalDateTime.shouldNotBeBetween
 */
fun LocalDateTime.shouldBeBetween(a: LocalDateTime, b: LocalDateTime) = this shouldBe between(a, b)

/**
 * Asserts that this is NOT between [a] and [b]
 *
 * Verifies that this is not after [a] and before [b], comparing all fields in LocalDateTime.
 *
 * Opposite of [LocalDateTime.shouldBeBetween]
 *
 * ```
 *    val date = LocalDateTime.of(2019, 2, 15, 12, 0, 0)
 *    val firstDate = LocalDateTime.of(2019, 2, 16, 12, 0, 0)
 *    val secondDate = LocalDateTime.of(2019, 2, 17, 12, 0, 0)
 *
 *    date.shouldNotBeBetween(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = LocalDateTime.of(2019, 2, 16, 12, 0, 0)
 *    val firstDate = LocalDateTime.of(2019, 2, 15, 12, 0, 0)
 *    val secondDate = LocalDateTime.of(2019, 2, 17, 12, 0, 0)
 *
 *    date.shouldNotBeBetween(firstDate, secondDate)  // Assertion fails, date IS between firstDate and secondDate
 * ```
 *
 * @see LocalDateTime.shouldBeBetween
 */
fun LocalDateTime.shouldNotBeBetween(a: LocalDateTime, b: LocalDateTime) = this shouldNotBe between(a, b)

/**
 * Matcher that checks if LocalDateTime is between two other LocalDateTimes
 *
 * Verifies that LocalDateTime is after the first LocalDateTime and before the second LocalDateTime
 * For example, 20/03/2019 10:00:00 is between 19/03/2019 10:00:00 and 21/03/2019 10:00:00, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val date = LocalDateTime.of(2019, 2, 16, 12, 0, 0)
 *    val firstDate = LocalDateTime.of(2019, 2, 15, 12, 0, 0)
 *    val secondDate = LocalDateTime.of(2019, 2, 17, 12, 0, 0)
 *
 *    date shouldBe after(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = LocalDateTime.of(2019, 2, 15, 12, 0, 0)
 *    val firstDate = LocalDateTime.of(2019, 2, 16, 12, 0, 0)
 *    val secondDate = LocalDateTime.of(2019, 2, 17, 12, 0, 0)
 *
 *    date shouldNotBe between(firstDate, secondDate)   // Assertion passes
 * ```
 *
 * @see LocalDateTime.shouldBeBetween
 * @see LocalDateTime.shouldNotBeBetween
 */
fun between(a: LocalDateTime, b: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult {
      val passed = value.isAfter(a) && value.isBefore(b)
      return MatcherResult(
         passed,
         { "$value should be after $a and before $b" },
         {
            "$value should not be be after $a and before $b"
         })
   }
}

/**
 * Asserts that this is between [a] and [b]
 *
 * Verifies that this is after [a] and before [b], comparing all fields in ZonedDateTime.
 *
 * Opposite of [ZonedDateTime.shouldNotBeBetween]
 *
 * ```
 *    val date = ZonedDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val firstDate = ZonedDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    date.shouldBeBetween(firstDate, secondDate)  // Assertion passes
 *
 *
 *    val date = ZonedDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val firstDate = ZonedDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    date.shouldBeBetween(firstDate, secondDate)  // Assertion fails, date is NOT between firstDate and secondDate
 * ```
 *
 * @see ZonedDateTime.shouldNotBeBetween
 */
fun ZonedDateTime.shouldBeBetween(a: ZonedDateTime, b: ZonedDateTime) = this shouldBe between(a, b)

/**
 * Asserts that this is NOT between [a] and [b]
 *
 * Verifies that this is not after [a] and before [b], comparing all fields in ZonedDateTime.
 *
 * Opposite of [ZonedDateTime.shouldBeBetween]
 *
 * ```
 *    val date = ZonedDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val firstDate = ZonedDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    date.shouldNotBeBetween(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = ZonedDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val firstDate = ZonedDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    date.shouldNotBeBetween(firstDate, secondDate)  // Assertion fails, date IS between firstDate and secondDate
 * ```
 *
 * @see ZonedDateTime.shouldBeBetween
 */
fun ZonedDateTime.shouldNotBeBetween(a: ZonedDateTime, b: ZonedDateTime) = this shouldNotBe between(a, b)

/**
 * Matcher that checks if ZonedDateTime is between two other ZonedDateTimes
 *
 * Verifies that ZonedDateTime is after the first ZonedDateTime and before the second ZonedDateTime
 * For example, 20/03/2019 10:00:00 -03:00 America/Sao_Paulo is between 19/03/2019 10:00:00 -03:00 America/Sao_Paulo
 * and 21/03/2019 10:00:00 -03:00 America/Sao_Paulo, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val date = ZonedDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val firstDate = ZonedDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    date shouldBe after(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = ZonedDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val firstDate = ZonedDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *    val secondDate = ZonedDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
 *
 *    date shouldNotBe between(firstDate, secondDate)   // Assertion passes
 * ```
 *
 * @see ZonedDateTime.shouldBeBetween
 * @see ZonedDateTime.shouldNotBeBetween
 */
fun between(a: ZonedDateTime, b: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
   override fun test(value: ZonedDateTime): MatcherResult {
      val passed = value.isAfter(a) && value.isBefore(b)
      return MatcherResult(
         passed,
         { "$value should be after $a and before $b" },
         {
            "$value should not be be after $a and before $b"
         })
   }
}

/**
 * Asserts that this is between [a] and [b]
 *
 * Verifies that this is after [a] and before [b], comparing all fields in ZonedDateTime.
 *
 * Opposite of [OffsetDateTime.shouldNotBeBetween]
 *
 * ```
 *    val date = OffsetDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val firstDate = OffsetDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldBeBetween(firstDate, secondDate)  // Assertion passes
 *
 *
 *    val date = OffsetDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val firstDate = OffsetDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldBeBetween(firstDate, secondDate)  // Assertion fails, date is NOT between firstDate and secondDate
 * ```
 *
 * @see OffsetDateTime.shouldNotBeBetween
 */
fun OffsetDateTime.shouldBeBetween(a: OffsetDateTime, b: OffsetDateTime) = this shouldBe between(a, b)

/**
 * Asserts that this is NOT between [a] and [b]
 *
 * Verifies that this is not after [a] and before [b], comparing all fields in ZonedDateTime.
 *
 * Opposite of [OffsetDateTime.shouldBeBetween]
 *
 * ```
 *    val date = OffsetDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val firstDate = OffsetDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldNotBeBetween(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = OffsetDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val firstDate = OffsetDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldNotBeBetween(firstDate, secondDate)  // Assertion fails, date IS between firstDate and secondDate
 * ```
 *
 * @see OffsetDateTime.shouldBeBetween
 */
fun OffsetDateTime.shouldNotBeBetween(a: OffsetDateTime, b: OffsetDateTime) = this shouldNotBe between(a, b)

/**
 * Matcher that checks if OffsetDateTime is between two other OffsetDateTimes
 *
 * Verifies that OffsetDateTime is after the first OffsetDateTime and before the second OffsetDateTime
 * For example, 20/03/2019 10:00:00 -03:00 is between 19/03/2019 10:00:00 -03:00 and 21/03/2019 10:00:00 -03:00,
 * and the matcher will have a positive result for this comparison
 *
 * ```
 *    val date = OffsetDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val firstDate = OffsetDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date shouldBe after(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = OffsetDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val firstDate = OffsetDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = OffsetDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date shouldNotBe between(firstDate, secondDate)   // Assertion passes
 * ```
 *
 * @see OffsetDateTime.shouldBeBetween
 * @see OffsetDateTime.shouldNotBeBetween
 */
fun between(a: OffsetDateTime, b: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
   override fun test(value: OffsetDateTime): MatcherResult {
      val passed = value.isAfter(a) && value.isBefore(b)
      return MatcherResult(
         passed,
         { "$value should be after $a and before $b" },
         {
            "$value should not be be after $a and before $b"
         })
   }
}

/**
 * Asserts that the day of month inputted is equaled the date day
 *
 * ```
 *    val date = LocalDateTime.of(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveDayOfMonth(15) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveDayOfMonth(day: Int) = this.dayOfMonth shouldBe day

/**
 * Asserts that the day of year inputted is equaled the date day
 *
 * ```
 *    val date = LocalDateTime.of(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveDayOfYear(46) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveDayOfYear(day: Int) = this.dayOfYear shouldBe day

/**
 * Asserts that the day of year inputted is equaled the date day
 *
 * ```
 *    val date = LocalDateTime.of(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveDayOfWeek(FRIDAY) // Assertion passes
 *    date.shouldHaveDayOfWeek(5) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveDayOfWeek(day: Int) = this.dayOfWeek.value shouldBe day
infix fun LocalDateTime.shouldHaveDayOfWeek(day: DayOfWeek) = this.dayOfWeek shouldBe day

/**
 * Asserts that the month inputted is equaled the date month
 *
 * ```
 *    val date = LocalDateTime.of(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveMonth(2) // Assertion passes
 *    date.shouldHaveMonth(FEBRUARY) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveMonth(month: Int) = this.month.value shouldBe month
infix fun LocalDateTime.shouldHaveMonth(month: Month) = this.month shouldBe month

/**
 * Asserts that the hour inputted is equaled the date time hour
 *
 * ```
 *    val date = LocalDateTime.of(2019, 2, 15, 12, 10, 0, 0)
 *
 *    date.shouldHaveHour(12) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveHour(hour: Int) = this.hour shouldBe hour

/**
 * Asserts that the minute inputted is equaled the date time minute
 *
 * ```
 *    val date = LocalDateTime.of(2019, 2, 15, 12, 10, 0, 0)
 *
 *    date.shouldHaveMinute(10) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveMinute(minute: Int) = this.minute shouldBe minute

/**
 * Asserts that the second inputted is equaled the date time second
 *
 * ```
 *    val date = LocalDateTime.of(2019, 2, 15, 12, 10, 11, 0)
 *
 *    date.shouldHaveSecond(11) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveSecond(second: Int) = this.second shouldBe second

/**
 * Asserts that the nano inputted is equaled the date time nano
 *
 * ```
 *    val date = LocalDateTime.of(2019, 2, 15, 12, 10, 0, 12)
 *
 *    date.shouldHaveNano(10) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveNano(nano: Int) = this.nano shouldBe nano

/**
 * Asserts that this is equal to [other] using the [ChronoZonedDateTime.isEqual]
 *
 * Opposite of [ZonedDateTime.shouldNotHaveSameInstantAs]
 *
 * ```
 *    val date = ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = ZonedDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldHaveSameInstantAs(other)  // Assertion passes
 *
 *
 *    val date = ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldHaveSameInstantAs(other)  // Assertion fails, date is NOT equal to the other date
 * ```
 *
 * @see ZonedDateTime.shouldNotHaveSameInstant
 */
infix fun ZonedDateTime.shouldHaveSameInstantAs(other: ZonedDateTime) = this should haveSameInstantAs(other)

/**
 * Asserts that this is NOT equal to [other] using the [ChronoZonedDateTime.isEqual]
 *
 * Opposite of [ZonedDateTime.shouldHaveSameInstantAs]
 *
 * ```
 *    val date = ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldNotHaveSameInstantAs(other)  // Assertion passes
 *
 *
 *    val date = ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = ZonedDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldNotHaveSameInstantAs(other)  // Assertion fails, date is equal to the other date
 * ```
 *
 * @see ZonedDateTime.shouldHaveSameInstantAs
 */
infix fun ZonedDateTime.shouldNotHaveSameInstantAs(other: ZonedDateTime) = this shouldNot haveSameInstantAs(other)

/**
 * Matcher that checks if ZonedDateTime is equal to another ZonedDateTime using the
 * [ChronoZonedDateTime.isEqual]
 *
 *
 * ```
 *    val date = ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = ZonedDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.haveSameInstantAs(other)  // Assertion passes
 *
 *
 *    val date = ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.haveSameInstantAs(other)  // Assertion fails, date is NOT equal to the other date
 * ```
 *
 * @see ZonedDateTime.shouldHaveSameInstantAs
 * @see ZonedDateTime.shouldNotHaveSameInstantAs
 */
fun haveSameInstantAs(other: ZonedDateTime) = object : Matcher<ZonedDateTime> {
   override fun test(value: ZonedDateTime): MatcherResult =
      MatcherResult(
         passed = value.isEqual(other),
         failureMessageFn = { "$value should be equal to $other" },
         negatedFailureMessageFn = {
            "$value should not be equal to $other"
         })
}
