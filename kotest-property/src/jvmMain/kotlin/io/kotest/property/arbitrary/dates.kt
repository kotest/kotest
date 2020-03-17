package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
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
fun Arb.Companion.period(maxYear: Int = 10): Arb<Period> = arb(listOf(Period.ZERO)) {
   generateSequence {
      Period.of(
         (0..maxYear).random(it.random),
         (0..11).random(it.random),
         (0..31).random(it.random)
      )
   }
}

/**
 * Arberates a stream of random LocalDates
 *
 * This generator creates randomly generated LocalDates, in the range [[minYear, maxYear]].
 *
 * If any of the years in the range contain a leap year, the date [29/02/YEAR] will always be a constant value of this
 * generator.
 *
 * @see [localDateTime]
 * @see [localTime]
 */
fun Arb.Companion.localDate(minYear: Int = 1970, maxYear: Int = 2030): Arb<LocalDate> = object : Arb<LocalDate>() {

   override fun edgecases(): List<LocalDate> {
      val yearRange = (minYear..maxYear)
      val feb28Date = LocalDate.of(yearRange.random(), 2, 28)

      val feb29Year = yearRange.firstOrNull { Year.of(it).isLeap }
      val feb29Date = feb29Year?.let { LocalDate.of(it, 2, 29) }

      return listOfNotNull(feb28Date, feb29Date, LocalDate.of(minYear, 1, 1), LocalDate.of(maxYear, 12, 31))
   }

   override fun values(rs: RandomSource): Sequence<Sample<LocalDate>> = generateSequence {
      val minDate = LocalDate.of(minYear, 1, 1)
      val maxDate = LocalDate.of(maxYear, 12, 31)
      val days = ChronoUnit.DAYS.between(minDate, maxDate)
      Sample(minDate.plusDays(rs.random.nextLong(days + 1)))
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
fun Arb.Companion.localTime(): Arb<LocalTime> = arb(listOf(LocalTime.of(23, 59, 59), LocalTime.of(0, 0, 0))) {
   generateSequence {
      LocalTime.of(it.random.nextInt(24), it.random.nextInt(60), it.random.nextInt(60))
   }
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
): Arb<LocalDateTime> = object : Arb<LocalDateTime>() {

   override fun edgecases(): List<LocalDateTime> {
      val localDates = localDate(minYear, maxYear).edgecases()
      val times = localTime().edgecases()
      return localDates.flatMap { date -> times.map { date.atTime(it) } }
   }

   override fun values(rs: RandomSource): Sequence<Sample<LocalDateTime>> = generateSequence {
      val date = localDate(minYear, maxYear).single(rs)
      val time = localTime().single(rs)
      Sample(date.atTime(time))
   }
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
fun Arb.Companion.instant(range: InstantRange): Arb<Instant> = arb(listOf(range.start, range.endInclusive)) {
   sequence {
      range.random(it.random)
   }
}

/**
 * Arberates a stream of random [Instant]
 */
fun Arb.Companion.instant(minValue: Instant = Instant.MIN, maxValue: Instant = Instant.MAX) =
   instant(minValue..maxValue)
