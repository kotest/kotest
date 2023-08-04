package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Shrinker
import io.kotest.property.arbitrary.duration.DurationClassifier
import kotlin.random.nextLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Arbitrary [Duration]s.
 *
 * @param[range] constrains the generated durations to be within this range.
 * @param[unit] specifies [DurationUnit] of arbitrary [Duration]s; if not passed random unit will be picked.
 */
fun Arb.Companion.duration(
   range: ClosedRange<Duration> = (-Int.MAX_VALUE.seconds)..Int.MAX_VALUE.seconds,
   unit: DurationUnit? = null
): Arb<Duration> = durationWithUnit(range, unit?.let { Arb.constant(it) } ?: Arb.enum()).map { it.first }

/**
 * Arbitrary [Duration] with associated [DurationUnit].
 *
 * @param[range] constrains the generated durations to be within this range.
 * @param[unit] constraints the associated units
 */
fun Arb.Companion.durationWithUnit(
   range: ClosedRange<Duration> = (-Int.MAX_VALUE.seconds)..Int.MAX_VALUE.seconds,
   unit: Arb<DurationUnit> = Arb.enum(),
): Arb<Pair<Duration, DurationUnit>> {
   return ArbitraryBuilder.create { rs ->
      val durationUnit = unit.next(rs)

      Pair(
         rs.random
            .nextLong(range.start.toLong(durationUnit)..range.endInclusive.toLong(durationUnit))
            .toDuration(durationUnit)
            .coerceIn(range),
         durationUnit,
      )
   }.withEdgecaseFn { rs ->
      Pair(
         listOf(range.start, Duration.ZERO, range.endInclusive).random(rs.random),
         unit.next(rs),
      )
   }
      .withShrinker(DurationShrinker(range))
      .withClassifier(DurationClassifier(range))
      .build()
}

class DurationShrinker(
   private val range: ClosedRange<Duration>,
) : Shrinker<Pair<Duration, DurationUnit>> {

   override fun shrink(value: Pair<Duration, DurationUnit>): List<Pair<Duration, DurationUnit>> {
      return LongShrinker(range.start.toLong(value.second)..range.endInclusive.toLong(value.second))
         .shrink(value.first.toLong(value.second))
         .map { it.toDuration(value.second) to value.second }
         .distinctBy { it.first }
         .filterNot { it.first == value.first }
         .filter { it.first in range }
   }
}
