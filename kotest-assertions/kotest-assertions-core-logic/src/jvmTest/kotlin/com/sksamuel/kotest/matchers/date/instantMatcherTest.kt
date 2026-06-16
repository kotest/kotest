package com.sksamuel.kotest.matchers.date

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.date.plusOrMinus
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.date.shouldBeBetween
import io.kotest.matchers.date.shouldBeCloseTo
import io.kotest.matchers.date.shouldNotBeAfter
import io.kotest.matchers.date.shouldNotBeBefore
import io.kotest.matchers.date.shouldNotBeBetween
import io.kotest.matchers.date.shouldNotBeCloseTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds

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

      "current instant should be between past instant and future instant" {
         val currentInstant = Instant.now()
         val pastInstant = currentInstant.minusMillis(30000)
         val futureInstant = currentInstant.plusMillis(30000)

         currentInstant.shouldBeBetween(pastInstant, futureInstant)
      }

      "past instant should not be between current instant and future instant" {
         val currentInstant = Instant.now()
         val pastInstant = currentInstant.minusMillis(30000)
         val futureInstant = currentInstant.plusMillis(30000)

         pastInstant.shouldNotBeBetween(currentInstant, futureInstant)
      }

      "future instant should not be between past instant and current instant" {
         val currentInstant = Instant.now()
         val pastInstant = currentInstant.minusMillis(30000)
         val futureInstant = currentInstant.plusMillis(30000)

         futureInstant.shouldNotBeBetween(pastInstant, currentInstant)
      }

      "current instant and 5 nanos ago instant should be close to 5 nanoseconds each other" {
         val currentInstant = Instant.now()
         val fiveNanosAgoInstant = currentInstant.minusNanos(5L)

         currentInstant.shouldBeCloseTo(fiveNanosAgoInstant, 5L.nanoseconds)
         fiveNanosAgoInstant.shouldBeCloseTo(currentInstant, 5L.nanoseconds)
         currentInstant shouldBe (fiveNanosAgoInstant plusOrMinus 5L.nanoseconds)
         fiveNanosAgoInstant shouldBe (currentInstant plusOrMinus 5L.nanoseconds)
      }

      "current instant and 1500 millis ago instant should not be close to 1000 millis each other" {
         val currentInstant = Instant.now()
         val someMillisAgoInstant = currentInstant.minusMillis(1500L)

         currentInstant.shouldNotBeCloseTo(someMillisAgoInstant, 1000L.milliseconds)
         someMillisAgoInstant.shouldNotBeCloseTo(currentInstant, 1000L.milliseconds)
         currentInstant shouldNotBe (someMillisAgoInstant plusOrMinus 1000L.nanoseconds)
         someMillisAgoInstant shouldNotBe (currentInstant plusOrMinus 1000L.nanoseconds)
      }

   }
}
