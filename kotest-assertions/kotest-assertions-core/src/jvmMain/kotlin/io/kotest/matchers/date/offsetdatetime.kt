package io.kotest.matchers.date

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.time.OffsetDateTime
import kotlin.time.Duration

fun beInTodayODT() = object : Matcher<OffsetDateTime> {
   override fun test(value: OffsetDateTime): MatcherResult {
      val passed = value.toLocalDate() == OffsetDateTime.now().toLocalDate()
      return MatcherResult(
         passed,
         { "$value should be today" },
         {
            "$value should not be today"
         })
   }
}

/**
 * Asserts that the OffsetDateTime has a date component of today
 *
 * ```
 *      OffsetDateTime.now().shouldBeToday() // Assertion passes
 * ```
 */
fun OffsetDateTime.shouldBeToday() = this should beInTodayODT()


/**
 * Asserts that the OffsetDateTime does not have a date component of today
 *
 * ```
 *      OffsetDateTime.of(2009, Month.APRIL, 2,2,2).shouldNotBeToday() // Assertion passes
 * ```
 */
fun OffsetDateTime.shouldNotBeToday() = this shouldNot beInTodayODT()

/**
 * Asserts that this is equal to [other] using the [OffsetDateTime.isEqual]
 *
 * Opposite of [OffsetDateTime.shouldNotHaveSameInstantAs]
 *
 * ```
 *    val date = OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = OffsetDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldHaveSameInstantAs(other)  // Assertion passes
 *
 *
 *    val date = OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldHaveSameInstantAs(other)  // Assertion fails, date is NOT equal to the other date
 * ```
 *
 * @see OffsetDateTime.shouldNotHaveSameInstantAs
 */
infix fun OffsetDateTime.shouldHaveSameInstantAs(other: OffsetDateTime) = this should haveSameInstantAs(other)

/**
 * Asserts that this is NOT equal to [other] using the [OffsetDateTime.isEqual]
 *
 * Opposite of [OffsetDateTime.shouldHaveSameInstantAs]
 *
 * ```
 *    val date = OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldNotHaveSameInstantAs(other)  // Assertion passes
 *
 *
 *    val date = OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = OffsetDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.shouldNotHaveSameInstantAs(other)  // Assertion fails, date is equal to the other date
 * ```
 *
 * @see OffsetDateTime.shouldHaveSameInstantAs
 */
infix fun OffsetDateTime.shouldNotHaveSameInstantAs(other: OffsetDateTime) = this shouldNot haveSameInstantAs(other)

/**
 * Matcher that checks if OffsetDateTime is equal to another OffsetDateTime using the
 * [OffsetDateTime.isEqual]
 *
 *
 * ```
 *    val date = OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = OffsetDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.haveSameInstantAs(other)  // Assertion passes
 *
 *
 *    val date = OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *    val other = OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-3))
 *
 *    date.haveSameInstantAs(other)  // Assertion fails, date is NOT equal to the other date
 * ```
 *
 * @see OffsetDateTime.shouldHaveSameInstantAs
 * @see OffsetDateTime.shouldNotHaveSameInstantAs
 */
fun haveSameInstantAs(other: OffsetDateTime) = object : Matcher<OffsetDateTime> {
   override fun test(value: OffsetDateTime): MatcherResult =
      MatcherResult(
         passed = value.isEqual(other),
         failureMessageFn = { "$value should be equal to $other" },
         negatedFailureMessageFn = {
            "$value should not be equal to $other"
         })
}

infix fun OffsetDateTime.plusOrMinus(tolerance: Duration): OffsetDateTimeToleranceMatcher =
   OffsetDateTimeToleranceMatcher(this, tolerance)

class OffsetDateTimeToleranceMatcher(
   private val expected: OffsetDateTime,
   private val tolerance: Duration
): Matcher<OffsetDateTime> {
   override fun test(value: OffsetDateTime): MatcherResult {
      val positiveTolerance = tolerance.absoluteValue
      val lowerBound = expected.minusNanos(positiveTolerance.inWholeNanoseconds)
      val upperBound = expected.plusNanos(positiveTolerance.inWholeNanoseconds)
      val valueAsInstant = value.toInstant()
      val insideToleranceInterval = (lowerBound.toInstant() <= valueAsInstant) && (valueAsInstant <= upperBound.toInstant())
      return MatcherResult(
         insideToleranceInterval,
         { "$value should be equal to $expected with tolerance $tolerance (between $lowerBound and $upperBound)" },
         { "$value should not be equal to $expected with tolerance $tolerance (not between $lowerBound and $upperBound)" }
      )
   }

   infix fun plusOrMinus(tolerance: Duration): OffsetDateTimeToleranceMatcher =
      OffsetDateTimeToleranceMatcher(expected, tolerance)
}

