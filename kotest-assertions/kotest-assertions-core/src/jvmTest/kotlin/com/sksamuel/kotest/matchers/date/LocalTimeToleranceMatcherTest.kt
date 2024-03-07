package com.sksamuel.kotest.matchers.date

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.date.LocalTimeToleranceMatcher
import io.kotest.matchers.date.and
import io.kotest.matchers.date.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class LocalTimeToleranceMatcherTest: WordSpec() {
   private val oneAm = LocalTime.of(1, 0, 0)
   private val onePm = LocalTime.of(13, 0, 0)
   private val sixPm = LocalTime.of(18, 0, 0)
   init {
      "should" should {
         "pass when the whole range is inside one calendar day" {
             onePm shouldBe (LocalTime.of(13, 1, 0) plusOrMinus 2.minutes)
             onePm shouldBe (LocalTime.of(12, 59, 0) plusOrMinus 2.minutes)
         }

         "fail when the whole range is inside one calendar day" {
            shouldThrowAny {
               onePm shouldBe (LocalTime.of(13, 3, 0) plusOrMinus 2.minutes)
            }.message shouldBe "13:00 should be equal to 13:03 with tolerance 2m (between 13:01 and 13:05)"
            shouldThrowAny {
               onePm shouldBe (LocalTime.of(12, 57, 0) plusOrMinus 2.minutes)
            }.message shouldBe "13:00 should be equal to 12:57 with tolerance 2m (between 12:55 and 12:59)"
         }

         "pass when the range includes midnight" {
            val oneMinuteAfterMidnight = LocalTime.of(0, 1, 0)
            LocalTime.of(23, 59) shouldBe (oneMinuteAfterMidnight plusOrMinus 2.minutes)
            LocalTime.of(0, 0) shouldBe (oneMinuteAfterMidnight plusOrMinus 2.minutes)
            LocalTime.of(0, 2) shouldBe (oneMinuteAfterMidnight plusOrMinus 2.minutes)
         }

         "fail when the range includes midnight" {
            val oneMinuteAfterMidnight = LocalTime.of(0, 1, 0)
            shouldThrowAny {
               LocalTime.of(23, 58) shouldBe (oneMinuteAfterMidnight plusOrMinus 2.minutes)
            }.message shouldBe "23:58 should be equal to 00:01 with tolerance 2m (between 23:59 and 00:03 next day)"
            shouldThrowAny {
               LocalTime.of(0, 4) shouldBe (oneMinuteAfterMidnight plusOrMinus 2.minutes)
            }.message shouldBe "00:04 should be equal to 00:01 with tolerance 2m (between 23:59 and 00:03 next day)"
         }
      }

      "shouldNot" should {
         "fail when the whole range is inside one calendar day" {
            shouldThrowAny {
               onePm shouldNotBe (LocalTime.of(13, 1, 0) plusOrMinus 2.minutes)
            }.message shouldBe "13:00 should not be equal to 13:01 with tolerance 2m (not between 12:59 and 13:03)"
            shouldThrowAny {
               onePm shouldNotBe (LocalTime.of(12, 59, 0) plusOrMinus 2.minutes)
            }.message shouldBe "13:00 should not be equal to 12:59 with tolerance 2m (not between 12:57 and 13:01)"
         }

         "pass when the whole range is inside one calendar day" {
            onePm shouldNotBe (LocalTime.of(13, 3, 0) plusOrMinus 2.minutes)
            onePm shouldNotBe (LocalTime.of(12, 57, 0) plusOrMinus 2.minutes)
         }

         "fail when the range includes midnight" {
            val oneMinuteAfterMidnight = LocalTime.of(0, 1, 0)
            shouldThrowAny {
               LocalTime.of(23, 59) shouldNotBe (oneMinuteAfterMidnight plusOrMinus 2.minutes)
            }.message shouldBe "23:59 should not be equal to 00:01 with tolerance 2m (not between 23:59 and 00:03 next day)"
            shouldThrowAny {
               LocalTime.of(0, 0) shouldNotBe (oneMinuteAfterMidnight plusOrMinus 2.minutes)
            }.message shouldBe "00:00 should not be equal to 00:01 with tolerance 2m (not between 23:59 and 00:03 next day)"
            shouldThrowAny {
               LocalTime.of(0, 2) shouldNotBe (oneMinuteAfterMidnight plusOrMinus 2.minutes)
            }.message shouldBe "00:02 should not be equal to 00:01 with tolerance 2m (not between 23:59 and 00:03 next day)"
         }

         "pass when the range includes midnight" {
            val oneMinuteAfterMidnight = LocalTime.of(0, 1, 0)
            LocalTime.of(23, 58) shouldNotBe (oneMinuteAfterMidnight plusOrMinus 2.minutes)
            LocalTime.of(0, 4) shouldNotBe (oneMinuteAfterMidnight plusOrMinus 2.minutes)
         }
      }

      "rangeDescription" should {
          "works when interval is within day" {
             LocalTimeToleranceMatcher.rangeDescription(oneAm, sixPm) shouldBe "between 01:00 and 18:00"
          }

         "works when interval is spans two days" {
            LocalTimeToleranceMatcher.rangeDescription(sixPm, oneAm) shouldBe "between 18:00 and 01:00 next day"
         }
      }

      "validateTolerance" should {
          "pass when less than 12 hours" {
             shouldNotThrowAny {
                LocalTimeToleranceMatcher.validateTolerance(11.hours and 59.minutes)
             }
          }

         "fail when 12 hours" {
            shouldThrowAny {
               LocalTimeToleranceMatcher.validateTolerance(12.hours)
            }
         }
      }
   }
}
