package io.kotest.extensions.time

import java.io.Serializable
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class MutableClock(
   private var instant: Instant,
   private var zone: ZoneId,
) : Clock(), Serializable {
   fun withInstant(instant: Instant): Clock = MutableClock(instant, zone)

   override fun withZone(zone: ZoneId): Clock = MutableClock(instant, zone)

   override fun getZone(): ZoneId = zone

   override fun instant(): Instant = instant

   override fun millis(): Long = instant.toEpochMilli()

   override fun equals(other: Any?): Boolean {
      if (other == null || other !is MutableClock) return false
      return instant == other.instant && zone == other.zone
   }

   override fun hashCode(): Int = instant.hashCode().xor(zone.hashCode())

   override fun toString(): String = "MutableClock[$instant,$zone]"
}
