package com.sksamuel.kotest.matchers.date

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.date.and
import io.kotest.matchers.date.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ZonedDateTimeToleranceMatcherTest : WordSpec() {
   private val chicagoTimeZone = ZoneId.of("America/Chicago")
   private val newYorkTimeZone = ZoneId.of("America/New_York")

   init {
      "shouldBe" should {
         "mismatch below lower bound, same offset" {
            shouldThrowAny {
               ZonedDateTime.of(2023, 11, 14, 0, 59, 0, 0, chicagoTimeZone) shouldBe
                  (ZonedDateTime.of(2023, 11, 14, 1, 30, 0, 0, chicagoTimeZone) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T00:59-06:00[America/Chicago] should be equal to 2023-11-14T01:30-06:00[America/Chicago] with tolerance 30m (between 2023-11-14T01:00-06:00[America/Chicago] and 2023-11-14T02:00-06:00[America/Chicago])"
         }

         "mismatch below lower bound, another offset" {
            shouldThrowAny {
               ZonedDateTime.of(2023, 11, 14, 0, 59, 0, 0, chicagoTimeZone) shouldBe
                  (ZonedDateTime.of(2023, 11, 14, 0, 30, 0, 0, newYorkTimeZone) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T00:59-06:00[America/Chicago] should be equal to 2023-11-14T00:30-05:00[America/New_York] with tolerance 30m (between 2023-11-14T00:00-05:00[America/New_York] and 2023-11-14T01:00-05:00[America/New_York])"
         }

         "match exactly on lower bound" {
            ZonedDateTime.of(2023, 11, 14, 1, 1, 0, 0, chicagoTimeZone) shouldBe
               (ZonedDateTime.of(2023, 11, 14, 1, 31, 0, 0, chicagoTimeZone) plusOrMinus 30.minutes)
         }

         "match inside tolerance interval, same offset" {
            ZonedDateTime.of(2023, 11, 14, 1, 2, 0, 0, chicagoTimeZone) shouldBe
               (ZonedDateTime.of(2023, 11, 14, 1, 30, 0, 0, chicagoTimeZone) plusOrMinus (30.minutes and 30.seconds))
         }

         "match inside tolerance interval, another offset" {
            ZonedDateTime.of(2023, 11, 14, 1, 2, 0, 0, chicagoTimeZone) shouldBe
               (ZonedDateTime.of(2023, 11, 14, 2, 30, 0, 0, newYorkTimeZone) plusOrMinus (30.minutes and 30.seconds))
         }

         "match exactly on upper bound" {
            ZonedDateTime.of(2023, 11, 14, 2, 1, 0, 0, chicagoTimeZone) shouldBe
               (ZonedDateTime.of(2023, 11, 14, 1, 31, 0, 0, chicagoTimeZone) plusOrMinus 30.minutes)
         }

         "mismatch above upper bound, same offset" {
            shouldThrowAny {
               ZonedDateTime.of(2023, 11, 14, 2, 1, 0, 0, chicagoTimeZone) shouldBe
                  (ZonedDateTime.of(2023, 11, 14, 1, 30, 0, 0, chicagoTimeZone) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T02:01-06:00[America/Chicago] should be equal to 2023-11-14T01:30-06:00[America/Chicago] with tolerance 30m (between 2023-11-14T01:00-06:00[America/Chicago] and 2023-11-14T02:00-06:00[America/Chicago])"
         }

         "mismatch above upper bound, another offset" {
            shouldThrowAny {
               ZonedDateTime.of(2023, 11, 14, 2, 1, 0, 0, chicagoTimeZone) shouldBe
                  (ZonedDateTime.of(2023, 11, 14, 0, 30, 0, 0, newYorkTimeZone) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T02:01-06:00[America/Chicago] should be equal to 2023-11-14T00:30-05:00[America/New_York] with tolerance 30m (between 2023-11-14T00:00-05:00[America/New_York] and 2023-11-14T01:00-05:00[America/New_York])"
         }

         "handle negative duration" {
            ZonedDateTime.of(2023, 11, 14, 2, 1, 0, 0, chicagoTimeZone) shouldBe
               (ZonedDateTime.of(2023, 11, 14, 1, 31, 0, 0, chicagoTimeZone) plusOrMinus (-30.minutes))
         }

         "handle duration with multiple components" {
            ZonedDateTime.of(2023, 11, 14, 1, 29, 30, 0, chicagoTimeZone) shouldBe
               (ZonedDateTime.of(2023, 11, 14, 1, 31, 0, 0, chicagoTimeZone) plusOrMinus (1.minutes and 30.seconds))
         }
      }

      "shouldNot" should {
         "match below lower bound, same offset" {
            ZonedDateTime.of(2023, 11, 14, 0, 59, 0, 0, chicagoTimeZone) shouldNotBe
               (ZonedDateTime.of(2023, 11, 14, 1, 30, 0, 0, chicagoTimeZone) plusOrMinus 30.minutes)
         }

         "match below lower bound, another offset" {
            ZonedDateTime.of(2023, 11, 14, 0, 59, 0, 0, chicagoTimeZone) shouldNotBe
               (ZonedDateTime.of(2023, 11, 14, 2, 30, 0, 0, newYorkTimeZone) plusOrMinus 30.minutes)
         }

         "mismatch exactly on lower bound" {
            shouldThrowAny {
               ZonedDateTime.of(2023, 11, 14, 1, 1, 0, 0, chicagoTimeZone) shouldNotBe
                  (ZonedDateTime.of(2023, 11, 14, 1, 31, 0, 0, chicagoTimeZone) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T01:01-06:00[America/Chicago] should not be equal to 2023-11-14T01:31-06:00[America/Chicago] with tolerance 30m (not between 2023-11-14T01:01-06:00[America/Chicago] and 2023-11-14T02:01-06:00[America/Chicago])"
         }

         "mismatch inside tolerance interval, same offset" {
            shouldThrowAny {
               ZonedDateTime.of(2023, 11, 14, 1, 2, 0, 0, chicagoTimeZone) shouldNotBe
                  (ZonedDateTime.of(2023, 11, 14, 1, 30, 0, 0, chicagoTimeZone) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T01:02-06:00[America/Chicago] should not be equal to 2023-11-14T01:30-06:00[America/Chicago] with tolerance 30m (not between 2023-11-14T01:00-06:00[America/Chicago] and 2023-11-14T02:00-06:00[America/Chicago])"
         }

         "mismatch inside tolerance interval, another offset" {
            shouldThrowAny {
               ZonedDateTime.of(2023, 11, 14, 1, 31, 0, 0, chicagoTimeZone) shouldNotBe
                  (ZonedDateTime.of(2023, 11, 14, 2, 30, 0, 0, newYorkTimeZone) plusOrMinus 2.minutes)
            }.message shouldBe "2023-11-14T01:31-06:00[America/Chicago] should not be equal to 2023-11-14T02:30-05:00[America/New_York] with tolerance 2m (not between 2023-11-14T02:28-05:00[America/New_York] and 2023-11-14T02:32-05:00[America/New_York])"
         }

         "mismatch exactly on upper bound" {
            shouldThrowAny {
               ZonedDateTime.of(2023, 11, 14, 2, 1, 0, 0, chicagoTimeZone) shouldNotBe
                  (ZonedDateTime.of(2023, 11, 14, 1, 31, 0, 0, chicagoTimeZone) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T02:01-06:00[America/Chicago] should not be equal to 2023-11-14T01:31-06:00[America/Chicago] with tolerance 30m (not between 2023-11-14T01:01-06:00[America/Chicago] and 2023-11-14T02:01-06:00[America/Chicago])"
         }

         "match above upper bound" {
            ZonedDateTime.of(2023, 11, 14, 2, 1, 0, 0, chicagoTimeZone) shouldNotBe
               (ZonedDateTime.of(2023, 11, 14, 1, 30, 0, 0, chicagoTimeZone) plusOrMinus 30.minutes)
         }
      }
   }
}
