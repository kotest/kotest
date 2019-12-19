package com.sksamuel.kotest.matchers.date

import io.kotest.matchers.date.*
import io.kotest.specs.FreeSpec
import java.time.Instant

class InstantMatcherTest : FreeSpec() {
   init {
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
