package io.kotest.matchers.kotlinx.datetime

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlinx.datetime.*

/**
 * Matcher that checks if a LocalDateTime has a Date component of today
 *
 * It does this by checking it against Current Time, so if you are not using constant now listeners,
 * using this might fail if test run exactly on a date change.
 *
 * ```
 *     val date = Clock.System().todayAt(TimeZone.UTC)
 *
 *     date should beInToday() // Assertion passes
 *
 *
 *     val date = LocalDateTime(2018, Month.APRIL, 1, 3, 5)
 *
 *     date should beInToday() // Assertion fails
 * ```
 */
fun beInToday(timezone: TimeZone = TimeZone.UTC) = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): MatcherResult {
    val passed = value.date == Clock.System.todayIn(timezone)
    return MatcherResult(
      passed,
       { "$value should be today" },
       { "$value should not be today" }
    )
  }
}

/**
 * Matcher that checks if a LocalDate is today
 *
 * It does this by checking it against current time, so if you are not using constant now listeners,
 * using this might fail if test run exactly on a date change.
 *
 * ```
 *     val date = Clock.System().todayAt(TimeZone.UTC).date
 *
 *     date should beToday() // Assertion passes
 *
 *
 *     val date = LocalDate(2018,1,1)
 *
 *     date should beToday() // Assertion fails
 * ```
 */
fun beToday(timezone: TimeZone = TimeZone.UTC) = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): MatcherResult {
    val passed = value == Clock.System.todayIn(timezone)
    return MatcherResult(
      passed,
       { "$value should be today" },
       { "$value should not be today" }
    )
  }
}

/**
 * Asserts that the LocalDateTime has a date component of today
 *
 * ```
 *      Clock.System().todayAt(TimeZone.UTC).shouldBeToday() // Assertion passes
 * ```
 */
fun LocalDateTime.shouldBeToday(timezone: TimeZone = TimeZone.UTC) = this should beInToday(timezone)

/**
 * Asserts that the LocalDate is today
 *
 * ```
 *      Clock.System().todayAt(TimeZone.UTC).date.shouldBeToday() // Assertion passes
 * ```
 */
fun LocalDate.shouldBeToday(timezone: TimeZone = TimeZone.UTC) = this should beToday(timezone)

/**
 * Asserts that the LocalDateTime does not have a date component of today
 *
 * ```
 *      LocalDateTime(2009, Month.APRIL, 2,2,2).shouldNotBeToday() // Assertion passes
 * ```
 */
fun LocalDateTime.shouldNotBeToday(timezone: TimeZone = TimeZone.UTC) = this shouldNot beInToday(timezone)

/**
 * Asserts that the LocalDate is not today
 *
 * ```
 *      LocalDate(2009, Month.APRIL, 2).shouldNotBeToday() // Assertion passes
 * ```
 */
fun LocalDate.shouldNotBeToday(timezone: TimeZone = TimeZone.UTC) = this shouldNot beToday(timezone)


