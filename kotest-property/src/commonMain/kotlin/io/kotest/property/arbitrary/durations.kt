import io.kotest.property.Arb
import io.kotest.property.Shrinker
import io.kotest.property.arbitrary.ArbitraryBuilder
import io.kotest.property.arbitrary.duration.DurationClassifier
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Arbitrary [Duration]s.
 *
 * @param[range] constrain the generated durations to be within this range.
 */
fun Arb.Companion.duration(
   range: ClosedRange<Duration> = (-Int.MAX_VALUE.seconds)..Int.MAX_VALUE.seconds
): Arb<Duration> =
   ArbitraryBuilder.create { rs ->
      val unit = DurationUnit.values().random(rs.random)
      val value = rs.random.nextLong()
      value.toDuration(unit).coerceIn(range)
   }.withEdgecases(
      listOf(
         Duration.ZERO,
         Duration.INFINITE,
         Duration.INFINITE * -1,
      ).map { it.coerceIn(range) }
   ).withShrinker(DurationShrinker(range))
      .withClassifier(DurationClassifier(range))
      .build()

class DurationShrinker(
   private val range: ClosedRange<Duration>
) : Shrinker<Duration> {

   override fun shrink(value: Duration): List<Duration> {
      val unsafeShrinks = unsafeShrinks.mapNotNull { op ->
         try {
            op(value)
         } catch (_: IllegalArgumentException) {
            null
         }
      }

      val componentShrinks = componentsShrinker.shrink(value)

      return (unsafeShrinks + componentShrinks)
         .map { it.coerceIn(range) }
         .takeIf { shrinks -> shrinks.all { it != Duration.ZERO } }
         ?.distinct()
         ?: emptyList()
   }

//   private fun interface ShrinkUnsafe : (Duration) -> Duration
//   Implementing function interface is prohibited in JavaScript

   /** Tries to shrink, but might through an [IllegalArgumentException] if the resulting duration is invalid */
   private val unsafeShrinks: List<(Duration) -> Duration> =
      listOf(
         { d -> (d / 10).truncate(DurationUnit.MILLISECONDS) },
         { d -> (d / 100).truncate(DurationUnit.MILLISECONDS) },
         { d -> d * 10 },
         { d -> d * 100 },
      )

   private val componentsShrinker = Shrinker<Duration> { d ->
      d.toComponents { days, hours, minutes, seconds, nanoseconds ->
         listOf(
            days.days,
            hours.hours,
            minutes.minutes,
            seconds.seconds,
            nanoseconds.nanoseconds,
         )
      }
   }

   companion object {
      /** truncate the duration, removing any time units smaller than [unit] */
      private fun Duration.truncate(unit: DurationUnit): Duration =
         toLong(unit).toDuration(unit)
   }
}
