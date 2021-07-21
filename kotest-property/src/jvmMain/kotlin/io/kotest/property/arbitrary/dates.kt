package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.Year
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalQueries.localDate
import java.time.temporal.TemporalQueries.localTime
import kotlin.random.Random

/**
 * Arberates a random [Period]s.
 *
 * This generator creates randomly generated Periods, with years less than or equal to [maxYear].
 *
 * If [maxYear] is 0, only random months and days will be generated.
 *
 * Months will always be in range [0..11]
 * Days will always be in range [0..31]
 */
fun Arb.Companion.period(maxYear: Int = 10): Arb<Period> = arbitrary(listOf(Period.ZERO)) {
   Period.of(
      (0..maxYear).random(it.random),
      (0..11).random(it.random),
      (0..31).random(it.random)
   )
}

fun Arb.Companion.localDate() = Arb.Companion.localDate(LocalDate.of(1970, 1, 1), LocalDate.of(2030, 12, 31))

/**
 * Arberates a stream of random LocalDates
 *
 * This generator creates randomly generated LocalDates, in the range [[minDate, maxDate]].
 *
 * If any of the years in the range contain a leap year, the date [29/02/YEAR] will always be a constant value of this
 * generator.
 *
 * @see [localDateTime]
 * @see [localTime]
 */
fun Arb.Companion.localDate(
   minDate: LocalDate = LocalDate.of(1970, 1, 1),
   maxDate: LocalDate = LocalDate.of(2030, 12, 31)
): Arb<LocalDate> {

   val feb28DateThisYear = LocalDate.of(LocalDate.now().year, 2, 28)
   val minDateYear = if (minDate.isBefore(feb28DateThisYear)) minDate.year else minDate.year + 1
   val yearRange = (minDateYear..maxDate.year)

   val feb28Date = LocalDate.of(yearRange.random(), 2, 28)
   val feb29Year = yearRange.firstOrNull { Year.of(it).isLeap }
   val feb29Date = feb29Year?.let { LocalDate.of(it, 2, 29) }

   val edgeCases = listOfNotNull(
      feb28Date,
      feb29Date,
      LocalDate.of(minDate.year, minDate.month, minDate.dayOfMonth),
      LocalDate.of(maxDate.year, maxDate.month, maxDate.dayOfMonth)
   )

   return arbitrary(edgeCases) {
      val minDateGenerated = LocalDate.of(minDate.year, minDate.month, minDate.dayOfMonth)
      val maxDateGenerated = LocalDate.of(maxDate.year, maxDate.month, maxDate.dayOfMonth)
      val days = ChronoUnit.DAYS.between(minDateGenerated, maxDateGenerated)
      minDateGenerated.plusDays(it.random.nextLong(days + 1))
   }
}

/**
 * Arberates a stream of random LocalTimes
 *
 * This generator creates randomly generated LocalTimes.
 *
 * @see [localDateTime]
 * @see [localDate]
 */
fun Arb.Companion.localTime(): Arb<LocalTime> = arbitrary(listOf(LocalTime.of(23, 59, 59), LocalTime.of(0, 0, 0))) {
   LocalTime.of(it.random.nextInt(24), it.random.nextInt(60), it.random.nextInt(60))
}

/**
 * Arberates a stream of random LocalDateTimes
 *
 * This generator creates randomly generated LocalDates, in the range [[minYear, maxYear]].
 *
 * If any of the years in the range contain a leap year, the date [29/02/YEAR] will always be a constant value of this
 * generator.
 *
 * @see [localDateTime]
 * @see [localTime]
 */
fun Arb.Companion.localDateTime(
   minYear: Int = 1970,
   maxYear: Int = 2030
): Arb<LocalDateTime> {

   return arbitrary(
      edgecaseFn = {
         val date = localDate(LocalDate.of(minYear, 1, 1), LocalDate.of(maxYear, 12, 31)).edgecase(it)
         val time = localTime().edgecase(it)
         if (date == null || time == null) null else date.atTime(time)
      },
      sampleFn = {
         val date = localDate(LocalDate.of(minYear, 1, 1), LocalDate.of(maxYear, 12, 31)).single(it)
         val time = localTime().single(it)
         date.atTime(time)
      }
   )
}

typealias InstantRange = ClosedRange<Instant>

fun InstantRange.random(random: Random): Instant {
   try {
      val seconds = (start.epochSecond..endInclusive.epochSecond).random(random)

      val nanos = when {
         seconds == start.epochSecond && seconds == endInclusive.epochSecond -> start.nano..endInclusive.nano
         seconds == start.epochSecond -> endInclusive.nano..999_999_999
         seconds == start.epochSecond -> 0..endInclusive.nano
         else -> 0..999_999_999
      }.random(random)

      return Instant.ofEpochSecond(seconds, nanos.toLong())
   } catch (e: IllegalArgumentException) {
      throw NoSuchElementException(e.message)
   }
}

/**
 * Arberates a stream of random [Instant]
 */
fun Arb.Companion.instant(range: InstantRange): Arb<Instant> =
   arbitrary(listOf(range.start, range.endInclusive)) {
      range.random(it.random)
   }

/**
 * Arberates a stream of random [Instant]
 */
fun Arb.Companion.instant(
   minValue: Instant = Instant.MIN,
   maxValue: Instant = Instant.MAX
) = instant(minValue..maxValue)
