package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.random.Random
import kotlin.time.Instant

private val epochInstant = Instant.fromEpochMilliseconds(0L)

/**
 * Arberates a stream of random [Instant]
 */
fun Arb.Companion.kotlinInstant(
   range: KotlinInstantRange,
): Arb<Instant> =
   arbitrary(listOfNotNull(epochInstant.takeIf { it in range }, range.start, range.endInclusive)) {
      range.random(it.random)
   }

/**
 * Arberates a stream of random [Instant]
 */
fun Arb.Companion.kotlinInstant(
   minValue: Instant = Instant.DISTANT_PAST,
   maxValue: Instant = Instant.DISTANT_FUTURE,
): Arb<Instant> = kotlinInstant(minValue..maxValue)

typealias KotlinInstantRange = ClosedRange<Instant>

fun KotlinInstantRange.random(random: Random): Instant {
   try {
      val seconds = (start.epochSeconds..endInclusive.epochSeconds).random(random)

      val nanos = when (seconds) {
         start.epochSeconds if seconds == endInclusive.epochSeconds -> start.nanosecondsOfSecond..endInclusive.nanosecondsOfSecond
         start.epochSeconds -> start.nanosecondsOfSecond..999_999_999
         endInclusive.epochSeconds -> 0..endInclusive.nanosecondsOfSecond
         else -> 0..999_999_999
      }.random(random)

      return Instant.fromEpochSeconds(seconds, nanos.toLong())
   } catch (e: IllegalArgumentException) {
      throw NoSuchElementException(e.message)
   }
}
