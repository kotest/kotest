package io.kotest.matchers.date

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.be
import io.kotest.matchers.date.shouldHaveSameInstantAsZonedDateTime
import io.kotest.matchers.date.shouldNotHaveSameInstantAsZonedDateTime
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.time.OffsetDateTime
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

/**
 * Matcher that checks if a [ZonedDateTime] is within the given tolerance of another [ZonedDateTime].
 *
 * Comparisons performed via `shouldBe` and `shouldNotBe` are based on the instant in time represented by each
 * [ZonedDateTime], not on matching local date-time fields or time zones. This means values in different time zones
 * still match when they represent instants that are within the specified tolerance.
 *
 * ```
 *    val chicagoTimeZone = ZoneId.of("America/Chicago")
 *    val newYorkTimeZone = ZoneId.of("America/New_York")
 *
 *    ZonedDateTime.of(2023, 11, 14, 1, 2, 0, 0, chicagoTimeZone) shouldBe
 *       (ZonedDateTime.of(2023, 11, 14, 2, 30, 0, 0, newYorkTimeZone) plusOrMinus (30.minutes and 30.seconds))
 * ```
 */
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

/**
 * Asserts that ZonedDateTime is at the same Instant as an OffsetDateTime
 *
 * ```
 *         val date = ZonedDateTime.of(2026, 7, 14, 8, 0, 0, 0, ZoneId.of("America/New_York"))
 *         val other = OffsetDateTime.of(2026, 7, 14, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *         date should haveSameInstantAsOffsetDateTime(other)
 *         shouldThrow<AssertionError> {
 *            date shouldNot haveSameInstantAsOffsetDateTime(other)
 *         }
 *         date.plusSeconds(1L) shouldNot haveSameInstantAsOffsetDateTime(other)
 *         shouldThrow<AssertionError> {
 *            date.plusSeconds(1L) should haveSameInstantAsOffsetDateTime(other)
 *         }
 * ```
 */
infix fun ZonedDateTime.shouldHaveSameInstantAsOffsetDateTime(other: OffsetDateTime) = this should haveSameInstantAsOffsetDateTime(other)

/**
 * Asserts that ZonedDateTime is at the same Instant as a OffsetDateTime
 *
 * ```
 *         val date = ZonedDateTime.of(2026, 7, 14, 8, 0, 0, 0, ZoneId.of("America/New_York"))
 *         val other = OffsetDateTime.of(2026, 7, 14, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *         date should haveSameInstantAsOffsetDateTime(other)
 *         shouldThrow<AssertionError> {
 *            date shouldNot haveSameInstantAsOffsetDateTime(other)
 *         }
 *         date.plusSeconds(1L) shouldNot haveSameInstantAsOffsetDateTime(other)
 *         shouldThrow<AssertionError> {
 *            date.plusSeconds(1L) should haveSameInstantAsOffsetDateTime(other)
 *         }
 * ```
 */
infix fun ZonedDateTime.shouldNotHaveSameInstantAsOffsetDateTime(other: OffsetDateTime) = this shouldNot haveSameInstantAsOffsetDateTime(other)

/**
 * Matcher that checks if OffsetDateTime is at the same Instant as a ZonedDateTime
 *
 * ```
 *         val date = ZonedDateTime.of(2026, 7, 14, 8, 0, 0, 0, ZoneId.of("America/New_York"))
 *         val other = OffsetDateTime.of(2026, 7, 14, 11, 0, 0, 0, ZoneOffset.ofHours(-1))
 *         date should haveSameInstantAsOffsetDateTime(other)
 *         shouldThrow<AssertionError> {
 *            date shouldNot haveSameInstantAsOffsetDateTime(other)
 *         }
 *         date.plusSeconds(1L) shouldNot haveSameInstantAsOffsetDateTime(other)
 *         shouldThrow<AssertionError> {
 *            date.plusSeconds(1L) should haveSameInstantAsOffsetDateTime(other)
 *         }
 * ```
 *
 * @see ZonedDateTime.shouldHaveSameInstantAsOffsetDateTime
 * @see ZonedDateTime.shouldNotHaveSameInstantAsOffsetDateTime
 */
fun haveSameInstantAsOffsetDateTime(other: OffsetDateTime) = object : Matcher<ZonedDateTime> {
   override fun test(value: ZonedDateTime): MatcherResult =
      MatcherResult(
         passed = value.toInstant() == other.toInstant(),
         failureMessageFn = { "$value should be equal to $other" },
         negatedFailureMessageFn = {
            "$value should not be equal to $other"
         })
}
