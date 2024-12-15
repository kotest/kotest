package io.kotest.extensions.clock

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.time.Duration

/**
 * A mutable [Clock] that supports millisecond precision.
 */
class TestClock(
   private var instant: Instant,
   private val zone: ZoneId,
) : Clock() {

   companion object {
      fun utc(instant: Instant) = TestClock(instant, ZoneOffset.UTC)
   }

   override fun instant(): Instant = instant

   override fun withZone(zone: ZoneId): Clock {
      return TestClock(instant, zone)
   }

   override fun getZone(): ZoneId = zone

   /**
    * Sets the instant in this test clock to the given value.
    */
   fun setInstant(instant: Instant) {
      this.instant = instant
   }

   /**
    * Adds the given [duration] from the instant in this test clock.
    */
   operator fun plus(duration: Duration) {
      setInstant(instant.plusMillis(duration.inWholeMilliseconds))
   }

   /**
    * Removes the given [duration] from the instant in this test clock.
    */
   operator fun minus(duration: Duration) {
      setInstant(instant.minusMillis(duration.inWholeMilliseconds))
   }
}
