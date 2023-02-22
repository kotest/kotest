package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forNone
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.duration
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.take
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class DurationArbTest : WordSpec({

   "Arb.duration(-Duration.INFINITE..Duration.INFINITE)" should {
      val range = -Duration.INFINITE..Duration.INFINITE
      val arb = Arb.duration(range)

      "generate valid durations (no exceptions)" {
         shouldNotThrowAny {
            arb.take(1_000_000).toList()
         }
      }

      "should contain all edge cases" {
         arb.edgecases() shouldContainAll (listOf(
            -Duration.INFINITE,
            Duration.ZERO,
            Duration.INFINITE,
         ))
      }
   }

   "Arb.duration of two microseconds in DurationUnit.NANOSECONDS" should {
      val range = (-1).toDuration(DurationUnit.MICROSECONDS)..(1).toDuration(DurationUnit.MICROSECONDS)
      val arb = Arb.duration(
         range = range,
         unit = DurationUnit.NANOSECONDS
      )

      "generate all NANOSECONDS durations in range of two microseconds" {
         val durations = arb.take(100_000)
            .toSet()

         durations shouldContainAll ((-1000)..(1000)).map { it.toDuration(DurationUnit.NANOSECONDS) }
         durations.forAll {
            it shouldBeLessThanOrEqualTo range.endInclusive
            it shouldBeGreaterThanOrEqualTo range.start
         }
      }
   }

   "Arb.duration of two milliseconds in DurationUnit.MICROSECONDS" should {
      val range = (-1).toDuration(DurationUnit.MILLISECONDS)..(1).toDuration(DurationUnit.MILLISECONDS)
      val arb = Arb.duration(
         range = range,
         unit = DurationUnit.MICROSECONDS
      )

      "generate all MICROSECONDS durations in range of two milliseconds" {
         val durations = arb.take(100_000)
            .toSet()

         durations shouldContainAll ((-1000)..(1000)).map { it.toDuration(DurationUnit.MICROSECONDS) }
         durations.forAll {
            it shouldBeLessThanOrEqualTo range.endInclusive
            it shouldBeGreaterThanOrEqualTo range.start
         }
      }
   }

   "Arb.duration of two seconds in DurationUnit.MILLISECOND" should {
      val range = (-1).toDuration(DurationUnit.SECONDS)..(1).toDuration(DurationUnit.SECONDS)
      val arb = Arb.duration(
         range = range,
         unit = DurationUnit.MILLISECONDS
      )

      "generate all MILLISECONDS durations in range of two seconds" {
         val durations = arb.take(100_000)
            .toSet()

         durations shouldContainAll ((-1000)..(1000)).map { it.toDuration(DurationUnit.MILLISECONDS) }
         durations.forAll {
            it shouldBeLessThanOrEqualTo range.endInclusive
            it shouldBeGreaterThanOrEqualTo range.start
         }
      }
   }

   "Arb.duration of two minutes in DurationUnit.SECONDS" should {
      val range = (-1).toDuration(DurationUnit.MINUTES)..(1).toDuration(DurationUnit.MINUTES)
      val arb = Arb.duration(
         range = range,
         unit = DurationUnit.SECONDS
      )

      "generate all SECONDS durations in range of two minutes" {
         val durations = arb.take(10_000)
            .toSet()

         durations shouldContainAll ((-60)..(60)).map { it.toDuration(DurationUnit.SECONDS) }
         durations.forAll {
            it shouldBeLessThanOrEqualTo range.endInclusive
            it shouldBeGreaterThanOrEqualTo range.start
         }
      }
   }

   "Arb.duration of two hours in DurationUnit.MINUTES" should {
      val range = (-60).toDuration(DurationUnit.MINUTES)..(60).toDuration(DurationUnit.MINUTES)
      val arb = Arb.duration(
         range = range,
         unit = DurationUnit.MINUTES
      )

      "generate all MINUTES durations in range of two hours" {
         val durations = arb.take(10_000)
            .toSet()

         durations shouldContainAll ((-60)..(60)).map { it.toDuration(DurationUnit.MINUTES) }
         durations.forAll {
            it shouldBeLessThanOrEqualTo range.endInclusive
            it shouldBeGreaterThanOrEqualTo range.start
         }
      }
   }

   "Arb.duration of two days in DurationUnit.HOURS" should {
      val range = (-1).toDuration(DurationUnit.DAYS)..(1).toDuration(DurationUnit.DAYS)
      val arb = Arb.duration(
         range = range,
         unit = DurationUnit.HOURS
      )

      "generate all HOURS durations in range of two hours" {
         val durations = arb.take(10_000)
            .toSet()

         durations shouldContainAll ((-24)..(24)).map { it.toDuration(DurationUnit.HOURS) }
         durations.forAll {
            it shouldBeLessThanOrEqualTo range.endInclusive
            it shouldBeGreaterThanOrEqualTo range.start
         }
      }
   }

   "Arb.duration()" should {
      "generate N valid Durations (no exceptions)" {
         Arb.duration().generate(RandomSource.default()).take(10_000).toList()
            .size shouldBe 10_000
      }
   }

   "Arb.duration for non-positive range" should {
      val range = (-1).toDuration(DurationUnit.DAYS)..Duration.ZERO
      val arb = Arb.duration(
         range = range,
         unit = DurationUnit.HOURS
      )

      "should contain only one edge case" {
         arb.edgecases() shouldContainAll (listOf(
            Duration.ZERO,
         ))
      }

      "generate only non-positive durations" {
         val durations = arb.take(10_000)
            .toSet()

         durations.forNone {
            it.shouldBeGreaterThan(Duration.ZERO)
         }
      }
   }


   "Arb.duration for non-megative range" should {
      val range = Duration.ZERO..1.toDuration(DurationUnit.DAYS)
      val arb = Arb.duration(
         range = range,
         unit = DurationUnit.HOURS
      )

      "should contain only one edge case" {
         arb.edgecases() shouldContainAll (listOf(
            Duration.ZERO,
         ))
      }

      "generate only non-negative durations" {
         val durations = arb.take(10_000)
            .toSet()

         durations.forNone {
            it.shouldBeLessThan(Duration.ZERO)
         }
      }
   }
})
