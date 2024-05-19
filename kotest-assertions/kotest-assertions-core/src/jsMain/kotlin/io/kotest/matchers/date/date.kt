package io.kotest.matchers.date

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import kotlin.js.Date

/**
 * Asserts that hours in this [Date] are the same as the given date.
 *
 * Verifies that hours in this [Date] are the same as [other]'s hours, ignoring any other fields.
 * For example, 16:59:59:7777 has the same hours as 16:01:02:0001, and this assertion should pass for this comparison
 *
 * Opposite of [Date.shouldNotHaveSameHoursAs]
 *
 * ```
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(23, 1, 2, 3333)
 *
 *     firstTime shouldHaveSameHoursAs secondTime   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(16, 59, 30, 1000)
 *
 *     firstTime shouldHaveSameHoursAs secondTime   //  Assertion fails, 23 != 16
```
 */
infix fun Date.shouldHaveSameHoursAs(other: Date) = this should haveSameHours(other)

/**
 * Asserts that hours in this [Date] are NOT the same as [other]'s hours
 *
 * Verifies that hours in this time aren't the same as [other]'s hours, ignoring any other fields.
 * For example, 16:59:59:7777 doesn't have the same hours as 16:01:02:0001, and this assertion should pass for this comparison
 *
 * Opposite of [Date.shouldNotHaveSameHoursAs]
 *
 * ```
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(20, 59, 30, 1000)
 *
 *     firstTime shouldNotHaveSameHoursAs secondTime   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(23, 30, 25, 2222)
 *
 *     firstTime shouldNotHaveSameHoursAs secondTime   //  Assertion fails, 23 == 23
```
 */
infix fun Date.shouldNotHaveSameHoursAs(other: Date) = this shouldNot haveSameHours(other)

/**
 * Matcher that compares hours of Dates
 *
 * Verifies that two times have exactly the same hours, ignoring any other fields.
 * For example, 23:59:30:9999 has the same hours as 23:01:02:3333, and the matcher will have a positive result for this comparison
 *
 * ```
 *     val firstTime = Date(23, 59, 30, 1000)
 *     val secondTime = Date(23, 1, 2, 3333)
 *
 *     firstTime should haveSameHours(secondTime)   //  Assertion passes
 *
 *
 *     val firstTime = Date(23, 59, 30, 1000)
 *     val secondTime = Date(16, 59, 30, 1000)
 *
 *     firstTime shouldNot haveSameHours(secondTime)   //  Assertion passes
 * ```
 *
 * @see [Date.shouldHaveSameHoursAs]
 * @see [Date.shouldNotHaveSameHoursAs]
 */
fun haveSameHours(other: Date): Matcher<Date> = object : Matcher<Date> {
   override fun test(value: Date): MatcherResult =
      MatcherResult(
         value.getHours() == other.getHours(),
         { "$value should have hours ${other.getHours()}" },
         { "$value should not have hours ${other.getHours()}" }
      )
}

/**
 * Asserts that this is before [other]
 *
 * Verifies that this is before [other], comparing every field in the OffsetDateTime.
 * For example, 09/02/1998 00:00:00 -03:00 is before 09/02/1998 00:00:01 -03:00,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [Date.shouldNotBeBefore]
 *
 * ```
 *    val firstDate = Date(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = Date(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = Date(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = Date(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldBeBefore secondDate     // Assertion fails, firstDate is one second after secondDate
 * ```
 *
 * @see Date.shouldNotBeAfter
 */
infix fun Date.shouldBeBefore(other: Date) = this should before(other)

/**
 * Asserts that this is NOT before [other]
 *
 * Verifies that this is not before [other], comparing every field in the OffsetDateTime.
 * For example, 09/02/1998 00:00:01 -03:00 is not before 09/02/1998 00:00:00 -03:00,
 * and this assertion should pass for this comparison.
 *
 * Opposite of [Date.shouldBeBefore]
 *
 * ```
 *    val firstDate = Date(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = Date(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotBeBefore secondDate    // Assertion passes
 *
 *
 *    val firstDate = Date(1998, 2, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-3))
 *    val secondDate = Date(1998, 2, 9, 0, 0, 1, 0, ZoneOffset.ofHours(-3))
 *
 *    firstDate shouldNotBeBefore secondDate     // Assertion fails, firstDate is one second before secondDate and we didn't expect it
 * ```
 *
 * @see Date.shouldBeAfter
 */
infix fun Date.shouldNotBeBefore(other: Date) = this shouldNot before(other)

/**
 * Matcher that compares two Dates and checks whether one is before the other
 *
 * Verifies that two Date occurs in a certain order, checking that one happened before the other.
 * For example, 09/02/1998 00:00:00 -03:00 is before 09/02/1998 00:00:01 -03:00,
 * and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = Date(1998, 2, 9, 0, 0, 0, 0)
 *    val secondDate = Date(1998, 2, 9, 0, 0, 1, 0)
 *
 *    firstDate shouldBe before(secondDate)     // Assertion passes
 *
 *
 *    val firstDate = Date(1998, 2, 9, 0, 0, 1, 0)
 *    val secondDate = Date(1998, 2, 9, 0, 0, 0, 0)
 *
 *    firstDate shouldNotBe before(secondDate)  // Assertion passes
 * ```
 *
 * @see Date.shouldBeBefore
 * @see Date.shouldNotBeBefore
 */
fun before(date: Date): Matcher<Date> = object : Matcher<Date> {
   override fun test(value: Date): MatcherResult =
      MatcherResult(
         value.getTime() < date.getTime(),
         { "$value should be before $date" },
         {
            "$value should not be before $date"
         })
}

/**
 * Asserts that this is after [other]
 *
 * Verifies that this is after [other], comparing year, month and day.
 * For example, 09/02/1998 is after 08/02/1998, and this assertion should pass for this comparison.
 *
 * Opposite of [Date.shouldNotBeAfter]
 *
 * ```
 *    val firstDate = Date(1998, 2, 9)
 *    val secondDate = Date(1998, 2, 8)
 *
 *    firstDate shouldBeAfter secondDate  // Assertion passes
 *
 *
 *    val firstDate = Date(1998, 2, 9)
 *    val secondDate = Date(1998, 2, 10)
 *
 *    firstDate shouldBeAfter secondDate  // Assertion fails, firstDate is NOT after secondDate
 * ```
 *
 * @see Date.shouldNotBeBefore
 */
infix fun Date.shouldBeAfter(other: Date) = this should after(other)

/**
 * Asserts that this is NOT after [other]
 *
 * Verifies that this is not after [other], comparing year, month and day.
 * For example, 09/02/1998 is not after 10/02/1998, and this assertion should pass for this comparison.
 *
 * Opposite of [Date.shouldBeAfter]
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
 * @see Date.shouldBeBefore
 */
infix fun Date.shouldNotBeAfter(other: Date) = this shouldNot after(other)

/**
 * Matcher that compares two Dates and checks whether one is after the other
 *
 * Verifies that two Date occurs in a certain order, checking that one happened after the other.
 * For example, 10/02/1998 is after 09/02/1998, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstDate = Date(1998, 2, 9)
 *    val secondDate = Date(1998, 2, 8)
 *
 *    firstDate shouldBe after(secondDate ) // Assertion passes
 *
 *
 *    val firstDate = Date(1998, 2, 9)
 *    val secondDate = Date(1998, 2, 10)
 *
 *    firstDate shouldNotBe after(secondDate)   // Assertion passes
 * ```
 *
 * @see Date.shouldBeAfter
 * @see Date.shouldNotBeAfter
 */
fun after(other: Date): Matcher<Date> = object : Matcher<Date> {
   override fun test(value: Date): MatcherResult =
      MatcherResult(
         value.getTime() > other.getTime(),
         { "$value should be after $other" },
         {
            "$value should not be after $other"
         })
}

/**
 * Asserts that the date has the given year.
 *
 * ```
 *    val date = Date(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveDayOfWeek(2019) // Assertion passes
 * ```
 */
infix fun Date.shouldHaveYear(year: Int) = this.getFullYear() shouldBe year


/**
 * Asserts that the date has the given day of week 0-6.
 *
 * ```
 *    val date = Date(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveDayOfWeek(5) // Assertion passes
 * ```
 */
infix fun Date.shouldHaveDayOfWeek(day: Int) = this.getDay() shouldBe day

/**
 * Asserts that the date has the given day of month 1-31.
 *
 * ```
 *    val date = Date(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveDayOfWeek(5) // Assertion passes
 * ```
 */
infix fun Date.shouldHaveDayOfMonth(day: Int) = this.getDate() shouldBe day

/**
 * Asserts that the month inputted is equaled the date month
 *
 * ```
 *    val date = Date(2019, 2, 15, 12, 0, 0, 0)
 *
 *    date.shouldHaveMonth(2) // Assertion passes
 * ```
 */
infix fun Date.shouldHaveMonth(month: Int) = this.getMonth() shouldBe month

/**
 * Asserts that the hour inputted is equaled the date time hour
 *
 * ```
 *    val date = Date(2019, 2, 15, 12, 10, 0, 0)
 *
 *    date.shouldHaveHour(12) // Assertion passes
 * ```
 */
infix fun Date.shouldHaveHour(hour: Int) = this.getHours() shouldBe hour

/**
 * Asserts that the minute inputted is equaled the date time minute
 *
 * ```
 *    val date = Date(2019, 2, 15, 12, 10, 0, 0)
 *
 *    date.shouldHaveMinute(10) // Assertion passes
 * ```
 */
infix fun Date.shouldHaveMinute(minute: Int) = this.getMinutes() shouldBe minute

/**
 * Asserts that the second inputted is equaled the date time second
 *
 * ```
 *    val date = Date(2019, 2, 15, 12, 10, 11, 0)
 *
 *    date.shouldHaveSecond(11) // Assertion passes
 * ```
 */
infix fun Date.shouldHaveSecond(second: Int) = this.getSeconds() shouldBe second
