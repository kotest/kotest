package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.Period
import java.time.Year
import java.time.Year.isLeap
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalQueries.localDate
import java.time.temporal.TemporalQueries.localTime
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

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
 * Generates a stream of random [LocalDate] instances within the specified range.
 *
 * This generator creates random [LocalDate] values in the inclusive range from [minDate] to [maxDate].
 *
 * It includes special edge cases for testing purposes:
 * - If the range includes any leap years, the date **February 29th** of the first such leap year is included.
 * - If the range includes any century years (years divisible by 100), the date **January 1st** of the first such century year is included.
 * - If the range includes any millennium years (years divisible by 1000), the date **January 1st** of the first such millennium year is included.
 *
 * These edge cases are added to increase the likelihood of covering date-related boundary conditions in tests.
 *
 * @param minDate The minimum (earliest) date to generate (inclusive). Default is January 1st, 1970.
 * @param maxDate The maximum (latest) date to generate (inclusive). Default is December 31st, 2030.
 * @return An [Arb]<[LocalDate]> that generates dates within the specified range, including edge cases.
 *
 * @see localDateTime
 * @see localTime
 */
fun Arb.Companion.localDate(
   minDate: LocalDate = LocalDate.of(1970, 1, 1),
   maxDate: LocalDate = LocalDate.of(2030, 12, 31)
): Arb<LocalDate> {
   require(minDate <= maxDate) { "minDate must be before or equal to maxDate" }
   if (minDate == maxDate) return Arb.constant(minDate)

   val edgeCases = mutableListOf(minDate, maxDate)

   val leapYear = (minDate.year..maxDate.year).firstOrNull { isLeap(it.toLong()) }
   if (leapYear != null) { edgeCases += LocalDate.of(leapYear, 2, 29) }

   val centuryYear = (minDate.year..maxDate.year).firstOrNull { it % 100 == 0 }
   if (centuryYear != null) { edgeCases += LocalDate.of(centuryYear, 1, 1) }

   val millenniumYear = (minDate.year..maxDate.year).firstOrNull { it % 1000 == 0 }
   if (millenniumYear != null) { edgeCases += LocalDate.of(millenniumYear, 1, 1) }

   edgeCases.removeAll { it !in minDate..maxDate }

   return arbitrary(edgeCases) { rs ->
      val daysBetween = ChronoUnit.DAYS.between(minDate, maxDate)
      minDate.plusDays(rs.random.nextLong(daysBetween + 1))
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
   minYear: Int? = null,
   maxYear: Int
): Arb<LocalDateTime> {
   return localDateTime(minYear ?: 1970, maxYear)
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
   minYear: Int,
   maxYear: Int? = null
): Arb<LocalDateTime> {
   return localDateTime(minYear, maxYear ?: 2030)
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
   minYear: Int,
   maxYear: Int
): Arb<LocalDateTime> {

   return localDateTime(
      minLocalDateTime = LocalDateTime.of(minYear, 1, 1, 0, 0),
      maxLocalDateTime = LocalDateTime.of(maxYear, 12, 31, 23, 59)
   )
}

/**
 * Arberates a stream of random LocalDateTimes
 *
 * This generator creates randomly generated LocalDates, in the range [[minLocalDateTime, maxLocalDateTime]].
 *
 * If any of the years in the range contain a leap year, the date [29/02/YEAR] will always be a constant value of this
 * generator.
 *
 * @see [localDateTime]
 * @see [localTime]
 */
fun Arb.Companion.localDateTime(
   minLocalDateTime: LocalDateTime = LocalDateTime.of(1970, 1, 1, 0, 0),
   maxLocalDateTime: LocalDateTime = LocalDateTime.of(2030, 12, 31, 23, 59)
): Arb<LocalDateTime> {

   return arbitrary(
      edgecaseFn = {
         generateSequence {
            val date = localDate(minLocalDateTime.toLocalDate(), maxLocalDateTime.toLocalDate()).edgecase(it)
            val time = localTime().edgecase(it)
            if (date == null || time == null) null else date.atTime(time)
         }.find { !it.isBefore(minLocalDateTime) && !it.isAfter(maxLocalDateTime) }
      },
      sampleFn = {
         generateSequence {
            val date = localDate(minLocalDateTime.toLocalDate(), maxLocalDateTime.toLocalDate()).single(it)
            val time = localTime().single(it)
            date.atTime(time)
         }.first { !it.isBefore(minLocalDateTime) && !it.isAfter(maxLocalDateTime) }
      }
   )
}

/**
 * Arberates a stream of random Year
 *
 * This generator creates randomly generated Year, in the range [[minYear, maxYear]].
 */
fun Arb.Companion.year(
   minYear: Year = Year.of(1970),
   maxYear: Year = Year.of(2030)
): Arb<Year> {
   return arbitrary(listOf(minYear, maxYear)) {
      Year.of(it.random.nextInt(minYear.value..maxYear.value))
   }
}

/**
 * Arberates a stream of random YearMonth
 *
 * If any of the years in the range contain a leap year, the date [02/YEAR] will always be a constant value of this
 * generator.
 *
 * This generator creates randomly generated YearMonth, in the range [[minYearMonth, maxYearMonth]].
 *
 * @see [yearMonth]
 */
fun Arb.Companion.yearMonth(
   minYearMonth: YearMonth = YearMonth.of(1970, 1),
   maxYearMonth: YearMonth = YearMonth.of(2030, 12)
): Arb<YearMonth> {
   val leapYears = (minYearMonth.year..maxYearMonth.year).filter { isLeap(it.toLong()) }
   val february = leapYears.map { YearMonth.of(it, 2) }

   return arbitrary(february + minYearMonth + maxYearMonth) {
      minYearMonth.plusMonths(it.random.nextLong(ChronoUnit.MONTHS.between(minYearMonth, maxYearMonth)))
   }.filter { it in minYearMonth..maxYearMonth }
}

typealias InstantRange = ClosedRange<Instant>

fun InstantRange.random(random: Random): Instant {
   try {
      val seconds = (start.epochSecond..endInclusive.epochSecond).random(random)

      val nanos = when {
         seconds == start.epochSecond && seconds == endInclusive.epochSecond -> start.nano..endInclusive.nano
         seconds == start.epochSecond -> start.nano..999_999_999
         seconds == endInclusive.epochSecond -> 0..endInclusive.nano
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

/**
 * Arberates a stream of random [OffsetDateTime]
 */
fun Arb.Companion.offsetDateTime(
   minValue: LocalDateTime = LocalDateTime.of(1970, 1, 1, 0, 0),
   maxValue: LocalDateTime = LocalDateTime.of(2030, 12, 31, 23, 59),
   zoneOffset: Arb<ZoneOffset> = zoneOffset()
): Arb<OffsetDateTime> = Arb.bind(
   localDateTime(minValue, maxValue),
   zoneOffset
) { time, offset -> time.atOffset(offset) }

/**
 * Arberates a stream of random [OffsetDateTime]
 */
fun Arb.Companion.offsetDateTime(
   minValue: Instant,
   maxValue: Instant,
   zoneOffset: Arb<ZoneOffset> = zoneOffset()
): Arb<OffsetDateTime> = Arb.bind(
   instant(minValue, maxValue),
   zoneOffset
) { time, offset -> time.atOffset(offset) }

/**
 * Arberates a stream of random [ZonedDateTime]
 */
fun Arb.Companion.zonedDateTime(
   minValue: LocalDateTime = LocalDateTime.of(1970, 1, 1, 0, 0),
   maxValue: LocalDateTime = LocalDateTime.of(2030, 12, 31, 23, 59),
   zoneId: Arb<ZoneId> = zoneId()
): Arb<ZonedDateTime> = Arb.bind(
   localDateTime(minValue, maxValue),
   zoneId
) { time, zone -> time.atZone(zone) }

/**
 * Arberates a stream of random [Date]
 */
fun Arb.Companion.javaDate(
   minDate: String = "1970-01-01",
   maxDate: String = "2050-12-31",
   zoneId: Arb<ZoneId> = zoneId()
): Arb<Date> {
   return Arb.bind(
      localDate(LocalDate.parse(minDate), LocalDate.parse(maxDate)),
      zoneId
   ) { localDate, zone -> Date.from(localDate.atStartOfDay(zone).toInstant()) }
}

fun Arb.Companion.javaDate(
   minDate: Date,
   maxDate: Date,
   zoneId: Arb<ZoneId> = zoneId()
): Arb<Date> {
   val dateFormat = SimpleDateFormat("yyyy-mm-dd")
   return javaDate(
      minDate = dateFormat.format(minDate),
      maxDate = dateFormat.format(maxDate),
      zoneId = zoneId
   )
}
