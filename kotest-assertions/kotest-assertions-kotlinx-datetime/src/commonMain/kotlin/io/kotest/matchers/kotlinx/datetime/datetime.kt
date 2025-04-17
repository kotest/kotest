package io.kotest.matchers.kotlinx.datetime

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number

/**
 * Asserts that this year is the same as [date]'s year
 *
 * Verifies that this year is the same as [date]'s year, ignoring any other fields.
 * For example, 09/02/1998 has the same year as 10/03/1998, and this assertion should pass for this comparison
 *
 * Opposite of [LocalDate.shouldNotHaveSameYearAs]
 *
 * ```
 *     val firstDate = LocalDate(1998, 2, 9)
 *     val secondDate = LocalDate(1998, 3, 10)
 *
 *     firstDate shouldHaveSameYearAs secondDate   //  Assertion passes
 *
 *
 *     val firstDate = LocalDate(2018, 2, 9)
 *     val secondDate = LocalDate(1998, 2, 9)
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
 *    val firstDate = LocalDate(2018, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 9)
 *
 *    firstDate shouldNotHaveSameYearAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 3, 10)
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 3, 10)
 *
 *    firstDate should haveSameYear(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(2018, 2, 9)
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
         { "$value should not have year ${date.year}" }
      )
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 3, 10, 11, 30, 30)
 *
 *    firstDate shouldHaveSameYearAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(2018, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
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
 *    val firstDate = LocalDateTime(2018, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 3, 10, 11, 30, 30)
 *
 *    firstDate shouldNotHaveSameYearAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 3, 10, 1, 30, 30)
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 3, 10, 11, 30, 30)
 *
 *    firstDate should haveSameYear(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(2018, 2, 9, 10, 0, 0)
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
         { "$value should not have year ${date.year}" }
      )
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 3, 10)
 *
 *    firstDate should haveSameYear(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(2018, 2, 9)
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(2018, 2, 10)
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 3, 9)
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 3, 9)
 *
 *    firstDate shouldNotHaveSameMonthAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(2018, 2, 10)
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
         { "$value should not have month ${date.month}" }
      )
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(2018, 2, 10, 10, 0, 0)
 *
 *    firstDate should haveSameMonth(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 3, 9, 10, 0, 0)
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(2018, 2, 10, 11, 30, 30)
 *
 *    firstDate shouldHaveSameMonthAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 3, 9, 10, 0, 0)
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 3, 10, 11, 30, 30)
 *
 *    firstDate shouldNotHaveSameMonthAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(2018, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 10, 1, 30, 30)
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
         { "$value should not have month ${date.month}" }
      )
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(2018, 3, 9)
 *
 *    firstDate shouldHaveSameDayAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 10)
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 10)
 *
 *    firstDate shouldNotHaveSameDayAs secondDate    //  Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(2018, 3, 9)
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(2018, 3, 9)
 *
 *    firstDate should haveSameDay(secondDate)   //  Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 10)
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
         { "$value should not have day ${date.dayOfMonth}" }
      )
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 3, 9, 11, 30, 30)
 *
 *    firstDate shouldHaveSameDayAs secondDate   //  Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 10, 10, 0, 0)
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 10, 10, 0, 0)
 *
 *    firstDate shouldNotHaveSameDayAs secondDate    // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(2018, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 3, 9, 11, 30, 30)
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(2018, 3, 9, 11, 30, 30)
 *
 *    firstDate should haveSameDay(secondDate)   // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 10, 10, 0, 0)
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
         { "$value should not have day ${date.dayOfMonth}" }
      )
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 10)
 *
 *    firstDate shouldBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 10)
 *    val secondDate = LocalDate(1998, 2, 9)
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
 *    val firstDate = LocalDate(1998, 2, 10)
 *    val secondDate = LocalDate(1998, 2, 9)
 *
 *    firstDate shouldNotBeBefore secondDate    // Assertion passes



 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 10)
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 10)
 *
 *    firstDate shouldBe before(secondDate)     // Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 10)
 *    val secondDate = LocalDate(1998, 2, 9)
 *
 *    firstDate shouldNotBe before(secondDate)  // Assertion passes
 * ```
 *
 * @see LocalDate.shouldBeBefore
 * @see LocalDate.shouldNotBeBefore
 */
fun before(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
   override fun test(value: LocalDate): MatcherResult =
      MatcherResult(value < date, { "$value should be before $date" }, { "$value should not be before $date" })
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 0, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 9, 0, 0, 1)
 *
 *    firstDate shouldBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 9, 0, 0, 1)
 *    val secondDate = LocalDateTime(1998, 2, 9, 0, 0, 0)
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 0, 0, 1)
 *    val secondDate = LocalDateTime(1998, 2, 9, 0, 0, 0)
 *
 *    firstDate shouldNotBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 9, 0, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 9, 0, 0, 1)
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 0, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 9, 0, 0, 1)
 *
 *    firstDate shouldBe before(secondDate)     // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 9, 0, 0, 1)
 *    val secondDate = LocalDateTime(1998, 2, 9, 0, 0, 0)
 *
 *    firstDate shouldNotBe before(secondDate)  // Assertion passes
 * ```
 *
 * @see LocalDateTime.shouldBeBefore
 * @see LocalDateTime.shouldNotBeBefore
 */
fun before(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult =
      MatcherResult(value < date, { "$value should be before $date" }, { "$value should not be before $date" })
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 8)
 *
 *    firstDate shouldBeAfter secondDate  // Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 10)
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 10)
 *
 *    firstDate shouldNotBeAfter secondDate   // Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 10)
 *    val secondDate = LocalDate(1998, 2, 9)
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
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 8)
 *
 *    firstDate shouldBe after(secondDate ) // Assertion passes
 *
 *
 *    val firstDate = LocalDate(1998, 2, 9)
 *    val secondDate = LocalDate(1998, 2, 10)
 *
 *    firstDate shouldNotBe after(secondDate)   // Assertion passes
 * ```
 *
 * @see LocalDate.shouldBeAfter
 * @see LocalDate.shouldNotBeAfter
 */
fun after(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
   override fun test(value: LocalDate): MatcherResult =
      MatcherResult(value > date, { "$value should be after $date" }, { "$value should not be after $date" })
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 8, 10, 0, 0)
 *
 *    firstDate shouldBeAfter secondDate  // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 10, 10, 0, 0)
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 10, 10, 0, 0)
 *
 *    firstDate shouldNotBeAfter secondDate   // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 10, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
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
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 8, 10, 0, 0)
 *
 *    firstDate shouldBe after(secondDate ) // Assertion passes
 *
 *
 *    val firstDate = LocalDateTime(1998, 2, 9, 10, 0, 0)
 *    val secondDate = LocalDateTime(1998, 2, 10, 10, 0, 0)
 *
 *    firstDate shouldNotBe after(secondDate)   // Assertion passes
 * ```
 *
 * @see LocalDateTime.shouldBeAfter
 * @see LocalDateTime.shouldNotBeAfter
 */
fun after(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult =
      MatcherResult(value > date,
         { "$value should be after $date" },
         { "$value should not be after $date" }
      )
}

/**
 * Asserts that this is between [a] and [b]
 *
 * Verifies that this is after [a] and before [b], comparing year, month and day.
 *
 * Opposite of [LocalDate.shouldNotBeBetween]
 *
 * ```
 *    val date = LocalDate(2019, 2, 16)
 *    val firstDate = LocalDate(2019, 2, 15)
 *    val secondDate = LocalDate(2019, 2, 17)
 *
 *    date.shouldBeBetween(firstDate, secondDate)  // Assertion passes
 *
 *
 *    val date = LocalDate(2019, 2, 15)
 *    val firstDate = LocalDate(2019, 2, 16)
 *    val secondDate = LocalDate(2019, 2, 17)
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
 *    val date = LocalDate(2019, 2, 15)
 *    val firstDate = LocalDate(2019, 2, 16)
 *    val secondDate = LocalDate(2019, 2, 17)
 *
 *    date.shouldNotBeBetween(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = LocalDate(2019, 2, 16)
 *    val firstDate = LocalDate(2019, 2, 15)
 *    val secondDate = LocalDate(2019, 2, 17)
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
 *    val date = LocalDate(2019, 2, 16)
 *    val firstDate = LocalDate(2019, 2, 15)
 *    val secondDate = LocalDate(2019, 2, 17)
 *
 *    date shouldBe after(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = LocalDate(2019, 2, 15)
 *    val firstDate = LocalDate(2019, 2, 16)
 *    val secondDate = LocalDate(2019, 2, 17)
 *
 *    date shouldNotBe between(firstDate, secondDate)   // Assertion passes
 * ```
 *
 * @see LocalDate.shouldBeBetween
 * @see LocalDate.shouldNotBeBetween
 */
fun between(a: LocalDate, b: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
   override fun test(value: LocalDate): MatcherResult {
      val passed = value > a && value < b
      return MatcherResult(
         passed,
         { "$value should be after $a and before $b" },
         { "$value should not be be after $a and before $b" }
      )
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
 *    val date = LocalDateTime(2019, 2, 16, 12, 0, 0)
 *    val firstDate = LocalDateTime(2019, 2, 15, 12, 0, 0)
 *    val secondDate = LocalDateTime(2019, 2, 17, 12, 0, 0)
 *
 *    date.shouldBeBetween(firstDate, secondDate)  // Assertion passes
 *
 *
 *    val date = LocalDateTime(2019, 2, 15, 12, 0, 0)
 *    val firstDate = LocalDateTime(2019, 2, 16, 12, 0, 0)
 *    val secondDate = LocalDateTime(2019, 2, 17, 12, 0, 0)
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
 *    val date = LocalDateTime(2019, 2, 15, 12, 0, 0)
 *    val firstDate = LocalDateTime(2019, 2, 16, 12, 0, 0)
 *    val secondDate = LocalDateTime(2019, 2, 17, 12, 0, 0)
 *
 *    date.shouldNotBeBetween(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = LocalDateTime(2019, 2, 16, 12, 0, 0)
 *    val firstDate = LocalDateTime(2019, 2, 15, 12, 0, 0)
 *    val secondDate = LocalDateTime(2019, 2, 17, 12, 0, 0)
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
 *    val date = LocalDateTime(2019, 2, 16, 12, 0, 0)
 *    val firstDate = LocalDateTime(2019, 2, 15, 12, 0, 0)
 *    val secondDate = LocalDateTime(2019, 2, 17, 12, 0, 0)
 *
 *    date shouldBe after(firstDate, secondDate) // Assertion passes
 *
 *
 *    val date = LocalDateTime(2019, 2, 15, 12, 0, 0)
 *    val firstDate = LocalDateTime(2019, 2, 16, 12, 0, 0)
 *    val secondDate = LocalDateTime(2019, 2, 17, 12, 0, 0)
 *
 *    date shouldNotBe between(firstDate, secondDate)   // Assertion passes
 * ```
 *
 * @see LocalDateTime.shouldBeBetween
 * @see LocalDateTime.shouldNotBeBetween
 */
fun between(a: LocalDateTime, b: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult {
      val passed = value > a && value < b
      return MatcherResult(
         passed,
         { "$value should be after $a and before $b" },
         { "$value should not be be after $a and before $b" }
      )
   }
}

/**
 * Asserts that the day of month inputted is equaled the date day
 *
 * ```
 *    val date = LocalDateTime(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveDayOfMonth(15) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveDayOfMonth(day: Int) = this.dayOfMonth shouldBe day

/**
 * Asserts that the day of year inputted is equaled the date day
 *
 * ```
 *    val date = LocalDateTime(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveDayOfYear(46) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveDayOfYear(day: Int) = this.dayOfYear shouldBe day

/**
 * Asserts that the day of year inputted is equaled the date day
 *
 * ```
 *    val date = LocalDateTime(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveDayOfWeek(FRIDAY) // Assertion passes
 *    date.shouldHaveDayOfWeek(5) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveDayOfWeek(day: Int) = this.dayOfWeek.isoDayNumber shouldBe day
infix fun LocalDateTime.shouldHaveDayOfWeek(day: DayOfWeek) = this.dayOfWeek shouldBe day

/**
 * Asserts that the month inputted is equaled the date month
 *
 * ```
 *    val date = LocalDateTime(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveMonth(2) // Assertion passes
 *    date.shouldHaveMonth(FEBRUARY) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveMonth(month: Int) = this.month.number shouldBe month
infix fun LocalDateTime.shouldHaveMonth(month: Month) = this.month shouldBe month

/**
 * Asserts that the hour inputted is equaled the date time hour
 *
 * ```
 *    val date = LocalDateTime(2019, 2, 15, 12, 10, 0, 0)
 *
 *    date.shouldHaveHour(12) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveHour(hour: Int) = this.hour shouldBe hour

/**
 * Asserts that the minute inputted is equaled the date time minute
 *
 * ```
 *    val date = LocalDateTime(2019, 2, 15, 12, 10, 0, 0)
 *
 *    date.shouldHaveMinute(10) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveMinute(minute: Int) = this.minute shouldBe minute

/**
 * Asserts that the second inputted is equaled the date time second
 *
 * ```
 *    val date = LocalDateTime(2019, 2, 15, 12, 10, 11, 0)
 *
 *    date.shouldHaveSecond(11) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveSecond(second: Int) = this.second shouldBe second

/**
 * Asserts that the nano inputted is equaled the date time nano
 *
 * ```
 *    val date = LocalDateTime(2019, 2, 15, 12, 10, 0, 12)
 *
 *    date.shouldHaveNano(10) // Assertion passes
 * ```
 */
infix fun LocalDateTime.shouldHaveNano(nano: Int) = this.nanosecond shouldBe nano
