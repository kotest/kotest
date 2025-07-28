package io.kotest.matchers.date

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.be
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.time.ZonedDateTime
import kotlin.time.Duration

fun beInTodayZDT() = object : Matcher<ZonedDateTime> {
   override fun test(value: ZonedDateTime): MatcherResult {
      val passed = value.toLocalDate() == ZonedDateTime.now().toLocalDate()
      return MatcherResult(
         passed,
         { "$value should be today" },
         { "$value should not be today" }
      )
   }
}

/**
 * Asserts that the ZonedDateTime does not have a date component of today
 *
 * ```
 *      ZonedDateTime.of(2009, Month.APRIL, 2,2,2).shouldNotBeToday() // Assertion passes
 * ```
 */
fun ZonedDateTime.shouldNotBeToday() = this shouldNot beInTodayZDT()

/**
 * Asserts that the ZonedDateTime has a date component of today
 *
 * ```
 *      ZonedDateTime.now().shouldBeToday() // Assertion passes
 * ```
 */
fun ZonedDateTime.shouldBeToday() = this should beInTodayZDT()

/**
 * Matcher that uses `actual` timezone on the `expected` ZonedDateTime
 *
 *
 * ```
 *   ZonedDateTime.of(2019, 12, 10, 10, 0, 0, 0, ZoneOffset.UTC) shouldBe
 *       ZonedDateTime.of(2019, 12, 10, 4, 0, 0, 0, ZoneId.of("America/Chicago")).atSameZone() // Assertion passes
 * ```
 */
fun ZonedDateTime.atSameZone() = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): MatcherResult = be(withZoneSameInstant(value.zone)).test(value)
}


infix fun ZonedDateTime.plusOrMinus(tolerance: Duration): ZonedDateTimeToleranceMatcher =
   ZonedDateTimeToleranceMatcher(this, tolerance)

class ZonedDateTimeToleranceMatcher(
   private val expected: ZonedDateTime,
   private val tolerance: Duration
): Matcher<ZonedDateTime> {
   override fun test(value: ZonedDateTime): MatcherResult {
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

   infix fun plusOrMinus(tolerance: Duration): ZonedDateTimeToleranceMatcher =
      ZonedDateTimeToleranceMatcher(expected, tolerance)
}

