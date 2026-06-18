package com.sksamuel.kotest.matchers.date

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.date.and
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DurationTest: StringSpec() {
   init {
      "should add one minute and one millisecond" {
         (1.minutes and 1.milliseconds).inWholeNanoseconds shouldBe 60_001_000_000L
      }

      "should add several durations" {
         (1.days and 2.hours and 3.minutes and 4.seconds and 5.milliseconds).inWholeNanoseconds shouldBe 93_784_005_000_000L
      }
   }
}
