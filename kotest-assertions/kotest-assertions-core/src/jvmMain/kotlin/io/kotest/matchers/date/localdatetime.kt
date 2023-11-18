package io.kotest.matchers.date

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration

/**
 * Matcher that checks if a LocalDateTime has a Date component of today
 *
 * It does this by checking it against LocalDate.now(), so if you are not using constant now listeners,
 * using this might fail if test run exactly on a date change.
 *
 * ```
 *     val date = LocalDateTime.now()
 *
 *     date should beInToday() // Assertion passes
 *
 *
 *     val date = LocalDateTime.of(2018, Month.APRIL, 1, 3, 5)
 *
 *     date should beInToday() // Assertion fails
 * ```
 */
fun beInToday() = object : Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult {
      val passed = value.toLocalDate() == LocalDate.now()
      return MatcherResult(
         passed,
         { "$value should be today" },
         {
            "$value should not be today"
         })
   }
}

/**
 * Matcher that checks if a LocalDate is today
 *
 * It does this by checking it against LocalDate.now(), so if you are not using constant now listeners,
 * using this might fail if test run exactly on a date change.
 *
 * ```
 *     val date = LocalDate.now()
 *
 *     date should beToday() // Assertion passes
 *
 *
 *     val date = LocalDate.of(2018,1,1)
 *
 *     date should beToday() // Assertion fails
 * ```
 */
fun beToday() = object : Matcher<LocalDate> {
   override fun test(value: LocalDate): MatcherResult {
      val passed = value == LocalDate.now()
      return MatcherResult(
         passed,
         { "$value should be today" },
         {
            "$value should not be today"
         })
   }
}

/**
 * Asserts that the LocalDateTime has a date component of today
 *
 * ```
 *      LocalDateTime.now().shouldBeToday() // Assertion passes
 * ```
 */
fun LocalDateTime.shouldBeToday() = this should beInToday()

/**
 * Asserts that the LocalDate is today
 *
 * ```
 *      LocalDate.now().shouldBeToday() // Assertion passes
 * ```
 */
fun LocalDate.shouldBeToday() = this should beToday()

/**
 * Asserts that the LocalDateTime does not have a date component of today
 *
 * ```
 *      LocalDateTime.of(2009, Month.APRIL, 2,2,2).shouldNotBeToday() // Assertion passes
 * ```
 */
fun LocalDateTime.shouldNotBeToday() = this shouldNot beInToday()

/**
 * Asserts that the LocalDate is not today
 *
 * ```
 *      LocalDate.of(2009, Month.APRIL, 2).shouldNotBeToday() // Assertion passes
 * ```
 */
fun LocalDate.shouldNotBeToday() = this shouldNot beToday()

infix fun LocalDateTime.plusOrMinus(tolerance: Duration): LocalDateTimeToleranceMatcher =
   LocalDateTimeToleranceMatcher(this, tolerance)

class LocalDateTimeToleranceMatcher(
   private val expected: LocalDateTime,
   private val tolerance: Duration
): Matcher<LocalDateTime> {
   override fun test(value: LocalDateTime): MatcherResult {
      val positiveTolerance = tolerance.absoluteValue
      val lowerBound = expected.minusNanos(positiveTolerance.inWholeNanoseconds)
      val upperBound = expected.plusNanos(positiveTolerance.inWholeNanoseconds)
      val insideToleranceInterval = (lowerBound <= value) && (value <= upperBound)
      return MatcherResult(
         insideToleranceInterval,
         { "$value should be equal to $expected with tolerance $tolerance (between $lowerBound and $upperBound)" },
         { "$value should not be equal to $expected with tolerance $tolerance (not between $lowerBound and $upperBound)" }
      )
   }

   infix fun plusOrMinus(tolerance: Duration): LocalDateTimeToleranceMatcher =
      LocalDateTimeToleranceMatcher(expected, tolerance)
}

