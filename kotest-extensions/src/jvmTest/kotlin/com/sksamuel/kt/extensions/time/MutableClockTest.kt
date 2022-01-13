package com.sksamuel.kt.extensions.time

import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.time.MutableClock
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Instant
import java.time.ZoneId

class MutableClockTest : StringSpec() {
   private val zoneId = ZoneId.of("Europe/Warsaw")
   private val instantNow = Instant.now()
   private val clock = MutableClock(instantNow, zoneId)

   init {
      "Set instant for the future" {
         val modifiedClock = clock.withInstant(instantNow.plusSeconds(123))
         modifiedClock shouldNotBe clock
         modifiedClock.zone shouldBe clock.zone
         modifiedClock.millis() shouldNotBe clock.millis()
      }
      "Set instant for the past" {
         val modifiedClock = clock.withInstant(instantNow.minusSeconds(123))
         modifiedClock shouldNotBe clock
         modifiedClock.zone shouldBe clock.zone
         modifiedClock.millis() shouldNotBe clock.millis()
      }
      "Change time zone" {
         val modifiedClock = clock.withZone(ZoneId.of("Europe/Paris"))
         modifiedClock shouldNotBe clock
         modifiedClock.zone shouldNotBe clock.zone
         modifiedClock.millis() shouldBe clock.millis()
      }
   }
}
