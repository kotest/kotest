package io.kotlintest.properties

import java.io.File
import java.math.BigInteger
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.Year
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalQueries.localDate
import java.time.temporal.TemporalQueries.localTime
import java.util.UUID
import kotlin.random.Random

/**
 * Generates a stream of random Periods
 *
 * This generator creates randomly generated Periods, with years less than or equal to [maxYear].
 *
 * If [maxYear] is 0, only random months and days will be generated.
 *
 * Months will always be in range [0..11]
 * Days will always be in range [0..31]
 */
fun Gen.Companion.period(maxYear: Int = 10): Gen<Period> = object : Gen<Period> {

  override fun constants(): Iterable<Period> = listOf(Period.ZERO)
  override fun random(): Sequence<Period> = generateSequence {
    Period.of((0..maxYear).random(), (0..11).random(), (0..31).random())
  }
}

fun Gen.Companion.bigInteger(maxNumBits: Int = 32): Gen<BigInteger> = BigIntegerGen(maxNumBits)

/**
 * Returns a stream of values where each value is a randomly
 * chosen created File object. The file objects do not necessarily
 * exist on disk.
 */
fun Gen.Companion.file(): Gen<File> = object : Gen<File> {
  override fun constants(): Iterable<File> = emptyList()
  override fun random(): Sequence<File> = generateSequence { File(nextPrintableString(Random.nextInt(100))) }
}

/**
 * Generates a stream of random Durations
 *
 * This generator creates randomly generated Duration, of at most [maxDuration].
 */
fun Gen.Companion.duration(maxDuration: Duration = Duration.ofDays(10)): Gen<Duration> = object : Gen<Duration> {
  private val maxDurationInSeconds = maxDuration.seconds

  override fun constants(): Iterable<Duration> = listOf(Duration.ZERO)
  override fun random(): Sequence<Duration> = generateSequence { Duration.ofSeconds(Random.nextLong(maxDurationInSeconds)) }
}

fun Gen.Companion.uuid(): Gen<UUID> = object : Gen<UUID> {
  override fun constants(): Iterable<UUID> = emptyList()
  override fun random(): Sequence<UUID> = generateSequence { UUID.randomUUID() }
}

/**
 * Generates a stream of random LocalDates
 *
 * This generator creates randomly generated LocalDates, in the range [[minYear, maxYear]].
 *
 * If any of the years in the range contain a leap year, the date [29/02/YEAR] will always be a constant value of this
 * generator.
 *
 * @see [localDateTime]
 * @see [localTime]
 */
fun Gen.Companion.localDate(minYear: Int = 1970, maxYear: Int = 2030): Gen<LocalDate> = object : Gen<LocalDate> {
  override fun constants(): Iterable<LocalDate> {
    val yearRange = (minYear..maxYear)
    val feb28Date = LocalDate.of(yearRange.random(), 2, 28)

    val feb29Year = yearRange.firstOrNull { Year.of(it).isLeap }
    val feb29Date = feb29Year?.let { LocalDate.of(it, 2, 29) }

    return listOfNotNull(feb28Date, feb29Date, LocalDate.of(minYear, 1, 1), LocalDate.of(maxYear, 12, 31))
  }

  override fun random(): Sequence<LocalDate> = generateSequence {
    val minDate = LocalDate.of(minYear, 1, 1)
    val maxDate = LocalDate.of(maxYear, 12, 31)

    val days = ChronoUnit.DAYS.between(minDate, maxDate)

    minDate.plusDays(Random.Default.nextLong(days + 1))
  }
}

/**
 * Generates a stream of random LocalTimes
 *
 * This generator creates randomly generated LocalTimes.
 *
 * @see [localDateTime]
 * @see [localDate]
 */
fun Gen.Companion.localTime(): Gen<LocalTime> = object : Gen<LocalTime> {
  override fun constants(): Iterable<LocalTime> = listOf(LocalTime.of(23, 59, 59), LocalTime.of(0, 0, 0))
  override fun random(): Sequence<LocalTime> = generateSequence {
    LocalTime.of(Random.nextInt(24), Random.nextInt(60), Random.nextInt(60))
  }
}

/**
 * Generates a stream of random LocalDateTimes
 *
 * This generator creates randomly generated LocalDates, in the range [[minYear, maxYear]].
 *
 * If any of the years in the range contain a leap year, the date [29/02/YEAR] will always be a constant value of this
 * generator.
 *
 * @see [localDateTime]
 * @see [localTime]
 */
fun Gen.Companion.localDateTime(minYear: Int = 1970,
                                maxYear: Int = 2030): Gen<LocalDateTime> = object : Gen<LocalDateTime> {
  override fun constants(): Iterable<LocalDateTime> {
    val localDates = localDate(minYear, maxYear).constants()
    val times = localTime().constants()

    return localDates.flatMap { date -> times.map { date.atTime(it) } }
  }

  override fun random(): Sequence<LocalDateTime> {
    val dateSequence = localDate(minYear, maxYear).random().iterator()
    val timeSequence = localTime().random().iterator()

    return generateSequence { dateSequence.next().atTime(timeSequence.next()) }
  }
}

inline fun <reified T : Enum<T>> Gen.Companion.enum(): Gen<T> = object : Gen<T> {
  val values = T::class.java.enumConstants.toList()
  override fun constants(): Iterable<T> = values
  override fun random(): Sequence<T> = from(values).random()
}
