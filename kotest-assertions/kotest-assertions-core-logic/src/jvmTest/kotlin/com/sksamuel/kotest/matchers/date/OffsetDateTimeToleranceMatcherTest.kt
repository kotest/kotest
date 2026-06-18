package com.sksamuel.kotest.matchers.date

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.date.and
import io.kotest.matchers.date.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class OffsetDateTimeToleranceMatcherTest : WordSpec() {
   private val plusHour = ZoneOffset.of("+01:00")
   private val plusTwoHours = ZoneOffset.of("+02:00")

   init {
      "shouldBe" should {
         "mismatch below lower bound, same offset" {
            shouldThrowAny {
               OffsetDateTime.of(2023, 11, 14, 0, 59, 0, 0, plusHour) shouldBe
                  (OffsetDateTime.of(2023, 11, 14, 1, 30, 0, 0, plusHour) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T00:59+01:00 should be equal to 2023-11-14T01:30+01:00 with tolerance 30m (between 2023-11-14T01:00+01:00 and 2023-11-14T02:00+01:00)"
         }

         "mismatch below lower bound, another offset" {
            shouldThrowAny {
               OffsetDateTime.of(2023, 11, 14, 0, 59, 0, 0, plusHour) shouldBe
                  (OffsetDateTime.of(2023, 11, 14, 0, 30, 0, 0, plusTwoHours) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T00:59+01:00 should be equal to 2023-11-14T00:30+02:00 with tolerance 30m (between 2023-11-14T00:00+02:00 and 2023-11-14T01:00+02:00)"
         }

         "match exactly on lower bound" {
            OffsetDateTime.of(2023, 11, 14, 1, 1, 0, 0, plusHour) shouldBe
               (OffsetDateTime.of(2023, 11, 14, 1, 31, 0, 0, plusHour) plusOrMinus 30.minutes)
         }

         "match inside tolerance interval, same offset" {
            OffsetDateTime.of(2023, 11, 14, 1, 2, 0, 0, plusHour) shouldBe
               (OffsetDateTime.of(2023, 11, 14, 1, 30, 0, 0, plusHour) plusOrMinus 30.minutes)
         }

         "match inside tolerance interval, another offset" {
            OffsetDateTime.of(2023, 11, 14, 1, 2, 0, 0, plusHour) shouldBe
               (OffsetDateTime.of(2023, 11, 14, 2, 30, 0, 0, plusTwoHours) plusOrMinus 30.minutes)
         }

         "match exactly on upper bound" {
            OffsetDateTime.of(2023, 11, 14, 2, 1, 0, 0, plusHour) shouldBe
               (OffsetDateTime.of(2023, 11, 14, 1, 31, 0, 0, plusHour) plusOrMinus 30.minutes)
         }

         "mismatch above upper bound, same offset" {
            shouldThrowAny {
               OffsetDateTime.of(2023, 11, 14, 2, 1, 0, 0, plusHour) shouldBe
                  (OffsetDateTime.of(2023, 11, 14, 1, 30, 0, 0, plusHour) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T02:01+01:00 should be equal to 2023-11-14T01:30+01:00 with tolerance 30m (between 2023-11-14T01:00+01:00 and 2023-11-14T02:00+01:00)"
         }

         "mismatch above upper bound, another offset" {
            shouldThrowAny {
               OffsetDateTime.of(2023, 11, 14, 2, 1, 0, 0, plusHour) shouldBe
                  (OffsetDateTime.of(2023, 11, 14, 0, 30, 0, 0, plusTwoHours) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T02:01+01:00 should be equal to 2023-11-14T00:30+02:00 with tolerance 30m (between 2023-11-14T00:00+02:00 and 2023-11-14T01:00+02:00)"
         }

         "handle negative duration" {
            OffsetDateTime.of(2023, 11, 14, 2, 1, 0, 0, plusHour) shouldBe
               (OffsetDateTime.of(2023, 11, 14, 1, 31, 0, 0, plusHour) plusOrMinus (-30.minutes))
         }

         "handle duration with multiple components" {
            OffsetDateTime.of(2023, 11, 14, 1, 29, 30, 0, plusHour) shouldBe
               (OffsetDateTime.of(2023, 11, 14, 1, 31, 0, 0, plusHour) plusOrMinus (1.minutes and 30.seconds))
         }
      }

      "shouldNot" should {
         "match below lower bound, same offset" {
            OffsetDateTime.of(2023, 11, 14, 0, 59, 0, 0, plusHour) shouldNotBe
               (OffsetDateTime.of(2023, 11, 14, 1, 30, 0, 0, plusHour) plusOrMinus 30.minutes)
         }

         "match below lower bound, another offset" {
            OffsetDateTime.of(2023, 11, 14, 0, 59, 0, 0, plusHour) shouldNotBe
               (OffsetDateTime.of(2023, 11, 14, 2, 30, 0, 0, plusTwoHours) plusOrMinus 30.minutes)
         }

         "mismatch exactly on lower bound" {
            shouldThrowAny {
               OffsetDateTime.of(2023, 11, 14, 1, 1, 0, 0, plusHour) shouldNotBe
                  (OffsetDateTime.of(2023, 11, 14, 1, 31, 0, 0, plusHour) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T01:01+01:00 should not be equal to 2023-11-14T01:31+01:00 with tolerance 30m (not between 2023-11-14T01:01+01:00 and 2023-11-14T02:01+01:00)"
         }

         "mismatch inside tolerance interval, same offset" {
            shouldThrowAny {
               OffsetDateTime.of(2023, 11, 14, 1, 2, 0, 0, plusHour) shouldNotBe
                  (OffsetDateTime.of(2023, 11, 14, 1, 30, 0, 0, plusHour) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T01:02+01:00 should not be equal to 2023-11-14T01:30+01:00 with tolerance 30m (not between 2023-11-14T01:00+01:00 and 2023-11-14T02:00+01:00)"
         }

         "mismatch inside tolerance interval, another offset" {
            shouldThrowAny {
               OffsetDateTime.of(2023, 11, 14, 1, 31, 0, 0, plusHour) shouldNotBe
                  (OffsetDateTime.of(2023, 11, 14, 2, 30, 0, 0, plusTwoHours) plusOrMinus 2.minutes)
            }.message shouldBe "2023-11-14T01:31+01:00 should not be equal to 2023-11-14T02:30+02:00 with tolerance 2m (not between 2023-11-14T02:28+02:00 and 2023-11-14T02:32+02:00)"
         }

         "mismatch exactly on upper bound" {
            shouldThrowAny {
               OffsetDateTime.of(2023, 11, 14, 2, 1, 0, 0, plusHour) shouldNotBe
                  (OffsetDateTime.of(2023, 11, 14, 1, 31, 0, 0, plusHour) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T02:01+01:00 should not be equal to 2023-11-14T01:31+01:00 with tolerance 30m (not between 2023-11-14T01:01+01:00 and 2023-11-14T02:01+01:00)"
         }

         "match above upper bound" {
            OffsetDateTime.of(2023, 11, 14, 2, 1, 0, 0, plusHour) shouldNotBe
               (OffsetDateTime.of(2023, 11, 14, 1, 30, 0, 0, plusHour) plusOrMinus 30.minutes)
         }
      }
   }
}
