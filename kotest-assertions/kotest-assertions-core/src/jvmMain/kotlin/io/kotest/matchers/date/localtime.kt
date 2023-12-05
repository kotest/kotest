package io.kotest.matchers.date

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import java.time.LocalTime
import kotlin.time.Duration

/**
 * Asserts that hours in this time are the same as [time]'s hours
 *
 * Verifies that hours in this time are the same as [time]'s hours, ignoring any other fields.
 * For example, 16:59:59:7777 has the same hours as 16:01:02:0001, and this assertion should pass for this comparison
 *
 * Opposite of [LocalTime.shouldNotHaveSameHoursAs]
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
infix fun LocalTime.shouldHaveSameHoursAs(time: LocalTime) = this should haveSameHours(time)

/**
 * Asserts that hours in this time are NOT the same as [time]'s hours
 *
 * Verifies that hours in this time aren't the same as [time]'s hours, ignoring any other fields.
 * For example, 16:59:59:7777 doesn't have the same hours as 16:01:02:0001, and this assertion should pass for this comparison
 *
 * Opposite of [LocalTime.shouldNotHaveSameHoursAs]
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
infix fun LocalTime.shouldNotHaveSameHoursAs(time: LocalTime) = this shouldNot haveSameHours(time)

/**
 * Matcher that compares hours of LocalTimes
 *
 * Verifies that two times have exactly the same hours, ignoring any other fields.
 * For example, 23:59:30:9999 has the same hours as 23:01:02:3333, and the matcher will have a positive result for this comparison
 *
 * ```
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(23, 1, 2, 3333)
 *
 *     firstTime should haveSameHours(secondTime)   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(16, 59, 30, 1000)
 *
 *     firstTime shouldNot haveSameHours(secondTime)   //  Assertion passes
 * ```
 *
 * @see [LocalTime.shouldHaveSameHoursAs]
 * @see [LocalTime.shouldNotHaveSameHoursAs]
 */
fun haveSameHours(time: LocalTime): Matcher<LocalTime> = object : Matcher<LocalTime> {
  override fun test(value: LocalTime): MatcherResult =
     MatcherResult(
        value.hour == time.hour,
        { "$value should have hours ${time.hour}" },
        { "$value should not have hours ${time.hour}" }
     )
}

/**
 * Asserts that minutes in this time are the same as [time]'s minutes
 *
 * Verifies that minutes in this time are the same as [time]'s minutes, ignoring any other fields.
 * For example, 1:59:03:7777 has the same minutes as 2:59:22:3333, and this assertion should pass for this comparison
 *
 * Opposite of [LocalTime.shouldNotHaveSameMinutesAs]
 *
 * ```
 *     val firstTime = LocalTime.of(13, 59, 30, 1000)
 *     val secondTime = LocalTime.of(17, 59, 22, 3333)
 *
 *     firstTime shouldHaveSameMinutesAs secondTime   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(23, 1, 30, 1000)
 *
 *     firstTime shouldHaveSameMinutesAs secondTime   //  Assertion fails, 59 != 1
```
 */
infix fun LocalTime.shouldHaveSameMinutesAs(time: LocalTime) = this should haveSameMinutes(time)

/**
 * Asserts that minutes in this time are NOT the same as [time]'s minutes
 *
 * Verifies that minutes in this time aren't the same as [time]'s minutes, ignoring any other fields.
 * For example, 16:59:02:1111 doesn't have the same minutes as 16:01:02:1111, and this assertion should pass for this comparison
 *
 * Opposite of [LocalTime.shouldHaveSameMinutesAs]
 *
 * ```
 *     val firstTime = LocalTime.of(22, 59, 30, 1000)
 *     val secondTime = LocalTime.of(11, 59, 22, 3333)
 *
 *     firstTime shouldNotHaveSameMinutesAs secondTime   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(22, 59, 35, 2222)
 *
 *     firstTime shouldNotHaveSameMinutesAs secondTime   //  Assertion fails, 59 == 59
```
 */
infix fun LocalTime.shouldNotHaveSameMinutesAs(time: LocalTime) = this shouldNot haveSameMinutes(time)

/**
 * Matcher that compares minutes of LocalTimes
 *
 * Verifies that two times have exactly the same minutes, ignoring any other fields.
 * For example, 23:59:30:9999 has the same minutes as 12:59:02:3333, and the matcher will have a positive result for this comparison
 *
 * ```
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(12, 59, 2, 3333)
 *
 *     firstTime should haveSameMinutes(secondTime)   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(23, 20, 30, 1000)
 *
 *     firstTime shouldNot haveSameMinutes(secondTime)   //  Assertion passes
 * ```
 *
 * @see [LocalTime.shouldHaveSameMinutesAs]
 * @see [LocalTime.shouldNotHaveSameMinutesAs]
 */
fun haveSameMinutes(time: LocalTime): Matcher<LocalTime> = object : Matcher<LocalTime> {
  override fun test(value: LocalTime): MatcherResult =
     MatcherResult(value.minute == time.minute,
        { "$value should have minutes ${time.minute}" },
        { "$value should not have minutes ${time.minute}" }
     )
}

/**
 * Asserts that seconds in this time are the same as [time]'s seconds
 *
 * Verifies that seconds in this time are the same as [time]'s seconds, ignoring any other fields.
 * For example, 1:59:03:7777 has the same seconds as 2:33:03:3333, and this assertion should pass for this comparison
 *
 * Opposite of [LocalTime.shouldNotHaveSameSecondsAs]
 *
 * ```
 *     val firstTime = LocalTime.of(13, 59, 30, 1000)
 *     val secondTime = LocalTime.of(17, 22, 30, 3333)
 *
 *     firstTime shouldHaveSameSecondsAs secondTime   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(23, 59, 25, 1000)
 *
 *     firstTime shouldHaveSameSecondsAs secondTime   //  Assertion fails, 30 != 25
```
 */
infix fun LocalTime.shouldHaveSameSecondsAs(time: LocalTime) = this should haveSameSeconds(time)

/**
 * Asserts that seconds in this time are NOT the same as [time]'s seconds
 *
 * Verifies that seconds in this time aren't the same as [time]'s seconds, ignoring any other fields.
 * For example, 16:59:05:1111 doesn't have the same seconds as 16:59:02:1111, and this assertion should pass for this comparison
 *
 * Opposite of [LocalTime.shouldHaveSameSecondsAs]
 *
 * ```
 *     val firstTime = LocalTime.of(22, 59, 30, 1000)
 *     val secondTime = LocalTime.of(22, 59, 21, 1000)
 *
 *     firstTime shouldNotHaveSameSecondsAs secondTime   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 40, 30, 1000)
 *     val secondTime = LocalTime.of(11, 45, 30, 2222)
 *
 *     firstTime shouldNotHaveSameSecondsAs secondTime   //  Assertion fails, 30 == 30
```
 */
infix fun LocalTime.shouldNotHaveSameSecondsAs(time: LocalTime) = this shouldNot haveSameSeconds(time)

/**
 * Matcher that compares seconds of LocalTimes
 *
 * Verifies that two times have exactly the same seconds, ignoring any other fields.
 * For example, 23:17:30:9999 has the same seconds as 12:59:30:3333, and the matcher will have a positive result for this comparison
 *
 * ```
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(12, 27, 30, 3333)
 *
 *     firstTime should haveSameSeconds(secondTime)   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(23, 59, 45, 1000)
 *
 *     firstTime shouldNot haveSameSeconds(secondTime)   //  Assertion passes
 * ```
 *
 * @see [LocalTime.shouldHaveSameSecondsAs]
 * @see [LocalTime.shouldNotHaveSameSecondsAs]
 */
fun haveSameSeconds(time: LocalTime): Matcher<LocalTime> = object : Matcher<LocalTime> {
  override fun test(value: LocalTime): MatcherResult =
     MatcherResult(value.second == time.second,
        { "$value should have seconds ${time.second}" },
        { "$value should not have seconds ${time.second}" }
     )
}

/**
 * Asserts that nanos in this time are the same as [time]'s nanos
 *
 * Verifies that nanos in this time are the same as [time]'s nanos, ignoring any other fields.
 * For example, 1:59:15:7777 has the same nanos as 2:33:03:7777, and this assertion should pass for this comparison
 *
 * Opposite of [LocalTime.shouldNotHaveSameNanosAs]
 *
 * ```
 *     val firstTime = LocalTime.of(13, 59, 45, 1000)
 *     val nanoTime = LocalTime.of(17, 22, 30, 1000)
 *
 *     firstTime shouldHaveSameNanosAs nanoTime   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val nanoTime = LocalTime.of(23, 59, 30, 3333)
 *
 *     firstTime shouldHaveSameNanosAs nanoTime   //  Assertion fails, 1000 != 3333
```
 */
infix fun LocalTime.shouldHaveSameNanosAs(time: LocalTime) = this should haveSameNanos(time)

/**
 * Asserts that nanos in this time are NOT the same as [time]'s nanos
 *
 * Verifies that nanos in this time aren't the same as [time]'s nanos, ignoring any other fields.
 * For example, 16:59:05:2222 doesn't have the same nanos as 16:59:05:1111, and this assertion should pass for this comparison
 *
 * Opposite of [LocalTime.shouldHaveSameNanosAs]
 *
 * ```
 *     val firstTime = LocalTime.of(22, 59, 30, 1000)
 *     val secondTime = LocalTime.of(22, 59, 30, 3333)
 *
 *     firstTime shouldNotHaveSameNanosAs secondTime   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 40, 30, 1000)
 *     val secondTime = LocalTime.of(12, 17, 59, 1000)
 *
 *     firstTime shouldNotHaveSameNanosAs secondTime   //  Assertion fails, 1000 == 1000
```
 */
infix fun LocalTime.shouldNotHaveSameNanosAs(time: LocalTime) = this shouldNot haveSameNanos(time)

/**
 * Matcher that compares nanos of LocalTimes
 *
 * Verifies that two times have exactly the same nanos, ignoring any other fields.
 * For example, 23:17:30:9999 has the same nanos as 12:59:30:3333, and the matcher will have a positive result for this comparison
 *
 * ```
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(12, 27, 05, 1000)
 *
 *     firstTime should haveSameNanos(secondTime)   //  Assertion passes
 *
 *
 *     val firstTime = LocalTime.of(23, 59, 30, 1000)
 *     val secondTime = LocalTime.of(23, 59, 30, 2222)
 *
 *     firstTime shouldNot haveSameNanos(secondTime)   //  Assertion passes
 * ```
 *
 * @see [LocalTime.shouldHaveSameNanosAs]
 * @see [LocalTime.shouldNotHaveSameNanosAs]
 */
fun haveSameNanos(time: LocalTime): Matcher<LocalTime> = object : Matcher<LocalTime> {
  override fun test(value: LocalTime): MatcherResult =
     MatcherResult(
        value.nano == time.nano,
        { "$value should have nanos ${time.nano}" },
        { "$value should not have nanos ${time.nano}" }
     )
}

/**
 * Asserts that this is before [time]
 *
 * Verifies that this is before [time], comparing hours, minutes, seconds, nanos.
 * For example, 12:30:59:2222 is before 12:30:59:3333, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalTime.shouldNotBeBefore]
 *
 * ```
 *    val firstTime = LocalTime.of(12:30:59:2222)
 *    val secondTime = LocalTime.of(12:30:59:3333)
 *
 *    firstTime shouldBeBefore secondTime    // Assertion passes
 *
 *
 *    val firstTime = LocalTime.of(14:30:59:2222)
 *    val secondTime = LocalTime.of(12:30:59:2222)
 *
 *    firstTime shouldBeBefore secondTime     // Assertion fails, 14:30:59:2222 is not before 12:30:59:2222 as expected.
 * ```
 *
 * @see LocalTime.shouldNotBeAfter
 */
infix fun LocalTime.shouldBeBefore(time: LocalTime) = this should before(time)

/**
 * Asserts that this is NOT before [time]
 *
 * Verifies that this is not before [time], comparing hours, minutes, seconds, nanos.
 * For example, 12:30:59:2222 is not before 12:30:59:1111, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalTime.shouldBeBefore]
 *
 * ```
 *    val firstTime = LocalTime.of(12:30:59:2222)
 *    val secondTime = LocalTime.of(12:30:59:1111)
 *
 *    firstTime shouldNotBeBefore secondTime    // Assertion passes
 *
 *
 *    val firstTime = LocalTime.of(12:30:59:2222)
 *    val secondTime = LocalTime.of(12:30:59:3333)
 *
 *    firstTime shouldNotBeBefore secondTime     // Assertion fails, 12:30:59:2222 is before 12:30:59:3333, and we expected the opposite.
 * ```
 *
 * @see LocalTime.shouldBeAfter
 */
infix fun LocalTime.shouldNotBeBefore(time: LocalTime) = this shouldNot before(time)

/**
 * Matcher that compares two LocalTimes and checks whether one is before the other
 *
 * Verifies that two LocalTimes occurs in a certain order, checking that one happened before the other.
 * For example, 12:30:59:2222 is before 12:30:59:3333, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstTime = LocalTime.of(12:30:59:2222)
 *    val secondTime = LocalTime.of(12:30:59:3333)
 *
 *    firstTime shouldBe before(secondTime)     // Assertion passes
 *
 *
 *    val firstTime = LocalTime.of(12:30:59:2222)
 *    val secondTime = LocalTime.of(12:30:59:1111)
 *
 *    firstTime shouldNotBe before(secondTime)  // Assertion passes
 * ```
 *
 * @see LocalTime.shouldBeBefore
 * @see LocalTime.shouldNotBeBefore
 */
fun before(time: LocalTime): Matcher<LocalTime> = object : Matcher<LocalTime> {
  override fun test(value: LocalTime): MatcherResult =
     MatcherResult(
        value.isBefore(time),
        { "$value should be before $time" },
        { "$value should not be before $time" }
     )
}

/**
 * Asserts that this is after [time]
 *
 * Verifies that this is after [time], comparing hours, minutes, seconds, nanos.
 * For example, 12:30:59:2222 is after 10:22:59:2222, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalTime.shouldNotBeAfter]
 *
 * ```
 *    val firstTime = LocalTime.of(12, 30, 59, 1111)
 *    val secondTime = LocalTime.of(11, 30, 59, 1111)
 *
 *    firstTime shouldBeAfter secondTime  // Assertion passes
 *
 *
 *    val firstTime = LocalTime.of(12, 30, 59, 1111)
 *    val secondTime = LocalTime.of(12, 50, 59, 1111)
 *
 *    firstTime shouldBeAfter secondTime  // Assertion fails, firstTime is NOT after secondTime
 * ```
 *
 * @see LocalTime.shouldNotBeBefore
 */
infix fun LocalTime.shouldBeAfter(time: LocalTime) = this should after(time)

/**
 * Asserts that this is NOT after [time]
 *
 * Verifies that this is not after [time], comparing hours, minutes, seconds, nanos.
 * For example, 12:30:59:2222 is not after 12:30:59:3333, and this assertion should pass for this comparison.
 *
 * Opposite of [LocalTime.shouldBeAfter]
 *
 * ```
 *    val firstTime = LocalTime.of(12:30:59:2222)
 *    val secondTime = LocalTime.of(12:30:59:3333)
 *
 *    firstTime shouldNotBeAfter secondTime   // Assertion passes
 *
 *
 *    val firstTime = LocalTime.of(12:30:59:2222)
 *    val secondTime = LocalTime.of(12:30:59:1111)
 *
 *    firstTime shouldNotBeAfter secondTime   // Assertion fails, first time IS after secondTime
 * ```
 *
 * @see LocalTime.shouldBeBefore
 */
infix fun LocalTime.shouldNotBeAfter(time: LocalTime) = this shouldNot after(time)

/**
 * Matcher that compares two LocalTimes and checks whether one is after the other
 *
 * Verifies that two LocalTimes occurs in a certain order, checking that one happened after the other.
 * For example, 12:30:59:2222 is after 12:30:59:1111, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val firstTime = LocalTime.of(12:30:59:2222)
 *    val secondTime = LocalTime.of(09:30:59:2222)
 *
 *    firstTime shouldBe after(secondTime ) // Assertion passes
 *
 *
 *    val firstTime = LocalTime.of(12:30:59:2222)
 *    val secondTime = LocalTime.of(16:30:59:2222)
 *
 *    firstTime shouldNotBe after(secondTime)   // Assertion passes
 * ```
 *
 * @see LocalTime.shouldBeAfter
 * @see LocalTime.shouldNotBeAfter
 */
fun after(time: LocalTime): Matcher<LocalTime> = object : Matcher<LocalTime> {
  override fun test(value: LocalTime): MatcherResult =
     MatcherResult(
        value.isAfter(time),
        { "$value should be after $time" },
        { "$value should not be after $time" }
     )
}

/**
 * Asserts that this is between [a] and [b]
 *
 * Verifies that this is after [a] and before [b], comparing hours, minutes, seconds, nanos.
 *
 * Opposite of [LocalTime.shouldNotBeBetween]
 *
 * ```
 *    val time = LocalTime.of(12, 30, 59, 1111)
 *    val firstTime = LocalTime.of(11, 0, 0, 0)
 *    val secondTime = LocalTime.of(12, 31, 0, 0)
 *
 *    date.shouldBeBetween(firstTime, secondTime)  // Assertion passes
 *
 *
 *    val time = LocalTime.of(12, 30, 59, 1111)
 *    val firstTime = LocalTime.of(12, 30, 59, 2222)
 *    val secondTime = LocalTime.of(12, 30, 59, 3333)
 *
 *    date.shouldBeBetween(firstTime, secondTime)  // Assertion fails, time is NOT between firstTime and secondTime
 * ```
 *
 * @see LocalTime.shouldNotBeBetween
 */
fun LocalTime.shouldBeBetween(a: LocalTime, b: LocalTime) = this shouldBe between(a, b)

/**
 * Asserts that this is NOT between [a] and [b]
 *
 * Verifies that this is not after [a] and before [b], comparing hours, minutes, seconds, nanos.
 *
 * Opposite of [LocalTime.shouldBeBetween]
 *
 * ```
 *    val time = LocalTime.of(12, 30, 59, 1111)
 *    val firstTime = LocalTime.of(12, 30, 59, 2222)
 *    val secondTime = LocalTime.of(12, 30, 59, 3333)
 *
 *    time.shouldNotBeBetween(firstTime, secondTime) // Assertion passes
 *
 *
 *    val time = LocalTime.of(12, 30, 59, 1111)
 *    val firstTime = LocalTime.of(11, 0, 0, 0)
 *    val secondTime = LocalTime.of(12, 31, 0, 0)
 *
 *    time.shouldNotBeBetween(firstTime, secondTime)  // Assertion fails, time IS between firstTime and secondTime
 * ```
 *
 * @see LocalTime.shouldBeBetween
 */
fun LocalTime.shouldNotBeBetween(a: LocalTime, b: LocalTime) = this shouldNotBe between(a, b)

/**
 * Matcher that checks if LocalTime is between two other LocalTimes
 *
 * Verifies that LocalTime is after the first LocalTime and before the second LocalTime
 * For example, 12:30:59:2222 is between 12:30:59:1111 and 12:30:59:3333, and the matcher will have a positive result for this comparison
 *
 * ```
 *    val time = LocalTime.of(12, 30, 59, 1111)
 *    val firstTime = LocalTime.of(11, 0, 0, 0)
 *    val secondTime = LocalTime.of(12, 31, 0, 0)
 *
 *    time shouldBe after(firstTime, secondTime) // Assertion passes
 *
 *
 *    val time = LocalTime.of(12, 30, 59, 1111)
 *    val firstTime = LocalTime.of(12, 30, 59, 2222)
 *    val secondTime = LocalTime.of(12, 30, 59, 3333)
 *
 *    time shouldNotBe between(firstTime, secondTime)   // Assertion passes
 * ```
 *
 * @see LocalTime.shouldBeBetween
 * @see LocalTime.shouldNotBeBetween
 */
fun between(a: LocalTime, b: LocalTime): Matcher<LocalTime> = object : Matcher<LocalTime> {
  override fun test(value: LocalTime): MatcherResult {
    val passed = value.isAfter(a) && value.isBefore(b)
    return MatcherResult(
       passed,
       { "$value should be after $a and before $b" },
       { "$value should not be be after $a and before $b" }
    )
  }
}

/**
 * Matcher that checks if LocalTime is within tolerance of another LocalTime
 *
 * Verifies that LocalTime is after the first LocalTime and before the second LocalTime
 * For example, 12:30:00 is within 5 minutes of 12:34:59, and the matcher will have a positive result for this comparison.
 * It handles cases when one of these times is before midnight and another after midnight
 *
 * ```
 *    val time = LocalTime.of(12, 30, 0)
 *    val anotherTime = LocalTime.of(12, 34, 59)
 *
 *    time shouldBe (anotherTime plusOrMinus 5.minutes) // Assertion passes
 *    time shouldNotBe (anotherTime plusOrMinus 3.minutes) // Assertion fails
 *
 *    val beforeMidnight = LocalTime.of(23, 59, 0)
 *    val afterMidnight = LocalTime.of(0, 1, 0)
 *
 *    beforeMidnight shouldBe (afterMidnight plusOrMinus 3.minutes)   // Assertion passes
 *    afterMidnight shouldBe (beforeMidnight plusOrMinus 3.minutes)   // Assertion passes
 * ```
 *
 * @see LocalTime.shouldBeBetween
 * @see LocalTime.shouldNotBeBetween
 */
infix fun LocalTime.plusOrMinus(tolerance: Duration): LocalTimeToleranceMatcher =
   LocalTimeToleranceMatcher(this, tolerance)

class LocalTimeToleranceMatcher(
   private val expected: LocalTime,
   private val tolerance: Duration
): Matcher<LocalTime> {
   init {
      validateTolerance(tolerance)
   }

   override fun test(value: LocalTime): MatcherResult {
      val positiveTolerance = tolerance.absoluteValue
      val lowerBound = expected.minusNanos(positiveTolerance.inWholeNanoseconds)
      val upperBound = expected.plusNanos(positiveTolerance.inWholeNanoseconds)
      val spansTwoDays = upperBound < lowerBound
      val insideToleranceInterval = if(spansTwoDays) (lowerBound <= value) || (value <= upperBound)
         else (lowerBound <= value) && (value <= upperBound)
      return MatcherResult(
         insideToleranceInterval,
         { "$value should be equal to $expected with tolerance $tolerance (${rangeDescription(lowerBound, upperBound)})" },
         { "$value should not be equal to $expected with tolerance $tolerance (not ${rangeDescription(lowerBound, upperBound)})" }
      )
   }

   infix fun plusOrMinus(tolerance: Duration): LocalTimeToleranceMatcher =
      LocalTimeToleranceMatcher(expected, tolerance)

   companion object {
      const val NANOS_IN_SECOND = 1_000_000_000L
      const val SECONDS_IN_HOUR = 3_600L
      const val MAX_TOLERANCE = 12 * SECONDS_IN_HOUR * NANOS_IN_SECOND - 1

      internal fun rangeDescription(lowerBound: LocalTime, upperBound: LocalTime) =
         "between $lowerBound and $upperBound${if (upperBound < lowerBound) " next day" else ""}"

      internal fun validateTolerance(tolerance: Duration) {
         require(tolerance.absoluteValue.inWholeNanoseconds <= MAX_TOLERANCE) {
            "Tolerance cannot be 12 hours or more, was: $tolerance"
         }
      }
   }
}

