package io.kotest.common

import io.kotest.mpp.timeInMillis
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Executes the given [block] and returns elapsed time in milliseconds.
 */
@KotestInternal
@SoftDeprecated("temp fix for breaking change in Kotlin 1.6 -> 1.7")
inline fun measureTimeMillisCompat(block: () -> Unit): Long {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }
   val start = timeInMillis()
   block()
   return timeInMillis() - start
}


@KotestInternal
@SoftDeprecated("temp fix for breaking change in Kotlin 1.6 -> 1.7")
class TimeMarkCompat internal constructor(internal val startMillis: Long) {

   fun elapsedNow(): Duration = MonotonicTimeSourceCompat.elapsedFrom(this)

   operator fun plus(duration: Duration): TimeMarkCompat =
      MonotonicTimeSourceCompat.adjustReading(this, duration)

   operator fun minus(duration: Duration): TimeMarkCompat =
      MonotonicTimeSourceCompat.adjustReading(this, -duration)

   fun hasPassedNow(): Boolean = !elapsedNow().isNegative()

   fun hasNotPassedNow(): Boolean = elapsedNow().isNegative()
}


@KotestInternal
@SoftDeprecated("temp fix for breaking change in Kotlin 1.6 -> 1.7")
object MonotonicTimeSourceCompat {

   fun markNow(): TimeMarkCompat = TimeMarkCompat(timeInMillis())

   fun elapsedFrom(timeMark: TimeMarkCompat): Duration =
      (timeInMillis() - timeMark.startMillis).milliseconds

   fun adjustReading(timeMark: TimeMarkCompat, duration: Duration): TimeMarkCompat =
      TimeMarkCompat(timeMark.startMillis + duration.inWholeMilliseconds)
}
