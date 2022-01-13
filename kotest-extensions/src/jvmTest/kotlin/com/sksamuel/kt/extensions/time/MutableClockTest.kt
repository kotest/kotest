package com.sksamuel.kt.extensions.time

import io.kotest.core.spec.IsolationMode
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
      isolationMode = IsolationMode.InstancePerLeaf

      "Set instant for the future" {
         val modifiedClock = clock.withInstant(instantNow.plusSeconds(123))
         modifiedClock.zone shouldBe zoneId
         modifiedClock.millis() shouldNotBe instantNow.toEpochMilli()
      }
      "Set instant for the past" {
         val modifiedClock = clock.withInstant(instantNow.minusSeconds(123))
         modifiedClock.zone shouldBe zoneId
         modifiedClock.millis() shouldNotBe instantNow.toEpochMilli()
      }
      "Change time zone" {
         val modifiedClock = clock.withZone(ZoneId.of("Europe/Paris"))
         modifiedClock.zone shouldNotBe zoneId
         modifiedClock.millis() shouldBe instantNow.toEpochMilli()
      }
   }
}
