package io.kotest.property.kotlinx.datetime

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.next
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.nextInt

/**
 * Returns an [Arb] where each value is a [LocalDate], with a random day, and a year in the given range.
 *
 * The default year range is 1970 to the current year, as derived from the system clock and system timezone.
 */
fun Arb.Companion.date(
   yearRange: IntRange = 1970..Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
): Arb<LocalDate> = arbitrary {
   LocalDate(it.random.nextInt(yearRange), 1, 1).plus(it.random.nextInt(0..364), DateTimeUnit.DAY)
}

/**
 * Returns an [Arb] where each value is a [LocalDate] in the given range of dates.
 */
fun Arb.Companion.datesBetween(
   startDate: LocalDate,
   endDate: LocalDate,
): Arb<LocalDate> {
   val daysRange: IntRange = 0..(endDate - startDate).days
   return arbitrary(
      edgecases = listOf(startDate, endDate),
   ) {
      startDate.plus(it.random.nextInt(daysRange), DateTimeUnit.DAY)
   }
}

/**
 * Returns an [Arb] where each value is a [LocalDateTime], with a random day, random time, and a year
 * in the given range.
 *
 * The default year range is 1970 to the current year, as derived from the system clock and system timezone.
 * The default hour range is 0..23.
 * The default minute range is 0..59.
 * The default second range is 0..59.
 */
fun Arb.Companion.datetime(
   yearRange: IntRange = 1970..Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
   hourRange: IntRange = 0..23,
   minuteRange: IntRange = 0..59,
   secondRange: IntRange = 0..59,
): Arb<LocalDateTime> = arbitrary {
   Arb.date(yearRange)
      .next(it)
      .atTime(it.random.nextInt(hourRange), it.random.nextInt(minuteRange), it.random.nextInt(secondRange))
}
