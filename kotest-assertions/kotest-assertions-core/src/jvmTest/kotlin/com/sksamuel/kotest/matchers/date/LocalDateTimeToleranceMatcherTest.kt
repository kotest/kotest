package com.sksamuel.kotest.matchers.date

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.date.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.minutes

class LocalDateTimeToleranceMatcherTest : WordSpec() {
   init {
      "shouldBe" should {
         "mismatch below lower bound" {
            shouldThrowAny {
               LocalDateTime.of(2023, 11, 14, 0, 59) shouldBe
                  (LocalDateTime.of(2023, 11, 14, 1, 30) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T00:59 should be equal to 2023-11-14T01:30 with tolerance 30m (between 2023-11-14T01:00 and 2023-11-14T02:00)"
         }

         "match exactly on lower bound" {
            LocalDateTime.of(2023, 11, 14, 1, 1) shouldBe
               (LocalDateTime.of(2023, 11, 14, 1, 31) plusOrMinus 30.minutes)
         }

         "match inside tolerance interval" {
            LocalDateTime.of(2023, 11, 14, 1, 2) shouldBe
               (LocalDateTime.of(2023, 11, 14, 1, 30) plusOrMinus 30.minutes)
         }

         "match exactly on upper bound" {
            LocalDateTime.of(2023, 11, 14, 2, 1) shouldBe
               (LocalDateTime.of(2023, 11, 14, 1, 31) plusOrMinus 30.minutes)
         }

         "mismatch above upper bound" {
            shouldThrowAny {
               LocalDateTime.of(2023, 11, 14, 2, 1) shouldBe
                  (LocalDateTime.of(2023, 11, 14, 1, 30) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T02:01 should be equal to 2023-11-14T01:30 with tolerance 30m (between 2023-11-14T01:00 and 2023-11-14T02:00)"
         }
      }

      "shouldNot" should {
         "match below lower bound" {
            LocalDateTime.of(2023, 11, 14, 0, 59) shouldNotBe
               (LocalDateTime.of(2023, 11, 14, 1, 30) plusOrMinus 30.minutes)
         }

         "mismatch exactly on lower bound" {
            shouldThrowAny {
               LocalDateTime.of(2023, 11, 14, 1, 1) shouldNotBe
                  (LocalDateTime.of(2023, 11, 14, 1, 31) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T01:01 should not be equal to 2023-11-14T01:31 with tolerance 30m (not between 2023-11-14T01:01 and 2023-11-14T02:01)"
         }

         "mismatch inside tolerance interval" {
            shouldThrowAny {
               LocalDateTime.of(2023, 11, 14, 1, 2) shouldNotBe
                  (LocalDateTime.of(2023, 11, 14, 1, 30) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T01:02 should not be equal to 2023-11-14T01:30 with tolerance 30m (not between 2023-11-14T01:00 and 2023-11-14T02:00)"
         }

         "mismatch exactly on upper bound" {
            shouldThrowAny {
               LocalDateTime.of(2023, 11, 14, 2, 1) shouldNotBe
                  (LocalDateTime.of(2023, 11, 14, 1, 31) plusOrMinus 30.minutes)
            }.message shouldBe "2023-11-14T02:01 should not be equal to 2023-11-14T01:31 with tolerance 30m (not between 2023-11-14T01:01 and 2023-11-14T02:01)"
         }

         "match above upper bound" {
            LocalDateTime.of(2023, 11, 14, 2, 1) shouldNotBe
               (LocalDateTime.of(2023, 11, 14, 1, 30) plusOrMinus 30.minutes)
         }
      }
   }
}
