package com.sksamuel.kotest.matchers.date

import io.kotest.matchers.date.*
import io.kotest.specs.FreeSpec
import java.time.Instant

class InstantMatcherTest : FreeSpec() {
   init {
      "same instance of instant should be same" {
         val currentInstant = Instant.now()
         currentInstant shouldBe currentInstant
      }

      "different instance of instant having same time should be same" {
         Instant.ofEpochMilli(10000) shouldBe Instant.ofEpochMilli(10000)
      }

      "instance of different time should be different" {
         val currentInstant = Instant.now()
         val pastInstant = currentInstant.minusMillis(40000)
         currentInstant shouldNotBe pastInstant
      }

      "past instant should be before current instant" {
         val currentInstant = Instant.now()
         val pastInstant = currentInstant.minusMillis(1000)

         pastInstant shouldBeBefore currentInstant
      }

      "current instant should not be before past instant" {
         val currentInstant = Instant.now()
         val pastInstant = currentInstant.minusMillis(1000)

         currentInstant shouldNotBeBefore pastInstant
      }

      "future instant should be after current instant" {
         val currentInstant = Instant.now()
         val futureInstant = currentInstant.plusMillis(1000)

         futureInstant shouldBeAfter currentInstant
      }

      "current instant should not be after past instant" {
         val currentInstant = Instant.now()
         val futureInstant = currentInstant.plusMillis(1000)

         currentInstant shouldNotBeAfter futureInstant
      }

      "instant of same time should not be before another instant of same time" {
         Instant.ofEpochMilli(30000) shouldNotBeBefore Instant.ofEpochMilli(30000)
      }

      "instant of same time should not be after another instant of same time" {
         Instant.ofEpochMilli(30000) shouldNotBeAfter Instant.ofEpochMilli(30000)
      }

   }
}
