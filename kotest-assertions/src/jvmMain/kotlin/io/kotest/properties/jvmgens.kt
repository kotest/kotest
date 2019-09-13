package io.kotest.properties

import java.io.File
import java.math.BigInteger
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalQueries.localDate
import java.time.temporal.TemporalQueries.localTime
import java.util.*
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
   override fun random(seed: Long?): Sequence<Period> = generateSequence {
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
   override fun random(seed: Long?): Sequence<File> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence { File(r.nextPrintableString(r.nextInt(100))) }
   }
}

/**
 * Generates a stream of random Durations
 *
 * This generator creates randomly generated Duration, of at most [maxDuration].
 */
fun Gen.Companion.duration(maxDuration: Duration = Duration.ofDays(10)): Gen<Duration> = object : Gen<Duration> {
   private val maxDurationInSeconds = maxDuration.seconds

   override fun constants(): Iterable<Duration> = listOf(Duration.ZERO)
   override fun random(seed: Long?): Sequence<Duration> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence {
         Duration.ofSeconds(r.nextLong(maxDurationInSeconds))
      }
   }
}

fun Gen.Companion.uuid(): Gen<UUID> = object : Gen<UUID> {
   override fun constants(): Iterable<UUID> = emptyList()
   override fun random(seed: Long?): Sequence<UUID> = generateSequence { UUID.randomUUID() }
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

   override fun random(seed: Long?): Sequence<LocalDate> {
      val r = if (seed == null) Random.Default else Random(seed)
      val minDate = LocalDate.of(minYear, 1, 1)
      val maxDate = LocalDate.of(maxYear, 12, 31)
      val days = ChronoUnit.DAYS.between(minDate, maxDate)
      return generateSequence {
         minDate.plusDays(r.nextLong(days + 1))
      }
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
   override fun random(seed: Long?): Sequence<LocalTime> {
      val r = if (seed == null) Random.Default else Random(seed)
      return generateSequence {
         LocalTime.of(r.nextInt(24), r.nextInt(60), r.nextInt(60))
      }
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

   override fun random(seed: Long?): Sequence<LocalDateTime> {
      val dateSequence = localDate(minYear, maxYear).random().iterator()
      val timeSequence = localTime().random(seed).iterator()
      return generateSequence { dateSequence.next().atTime(timeSequence.next()) }
   }
}

inline fun <reified T : Enum<T>> Gen.Companion.enum(): Gen<T> = object : Gen<T> {
   val values = T::class.java.enumConstants.toList()
   override fun constants(): Iterable<T> = values
   override fun random(seed: Long?): Sequence<T> = from(values).random()
}

fun Gen.Companion.regex(regex: String) = RegexpGen(regex)
fun Gen.Companion.regex(regex: Regex) = regex(regex.pattern)

/**
 * Returns a stream of values where each value is a randomly
 * chosen File object from given directory. If the Directory does not exist, an empty sequence will be returned instead.
 * If recursive is true(default value is false) it gives files from inner directories as well recursively.
 */
fun Gen.Companion.file(directoryName: String, recursive: Boolean = false): Gen<File> = object : Gen<File> {
   override fun constants(): Iterable<File> = emptyList()
   override fun random(seed: Long?): Sequence<File> {
      val fileTreeWalk = File(directoryName).walk()
      return if (recursive) {
         randomiseFiles(fileTreeWalk.maxDepth(Int.MAX_VALUE), seed)
      } else randomiseFiles(fileTreeWalk.maxDepth(1), seed)
   }

   private fun randomiseFiles(files: Sequence<File>, seed: Long?): Sequence<File> {
      val allFiles = files.toList()
      if(allFiles.isEmpty()) return emptySequence()
      val random = if (seed == null) Random.Default else Random(seed)
      return generateInfiniteSequence { allFiles.random(random) }
   }
}
