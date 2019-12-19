package com.sksamuel.kotest.matchers.date

import io.kotest.matchers.date.*
import io.kotest.shouldBe
import io.kotest.shouldNotBe
import io.kotest.specs.FreeSpec
import java.sql.Timestamp
import java.time.Instant

class TimeStampTest : FreeSpec() {
   init {
      "two timestamp of same instance should be same" {
         val nowInstance = Instant.now()
         Timestamp.from(nowInstance) shouldBe Timestamp.from(nowInstance)
      }

      "two timestamp of different instance should be not be same" {
         val nowInstance = Instant.now()
         val anotherInstance = nowInstance.plusMillis(1000L)
         Timestamp.from(nowInstance) shouldNotBe Timestamp.from(anotherInstance)
      }

      "timestamp of current instance should be after the timestamp of past instance" {
         val nowInstance = Instant.now()
         val instanceBeforeFiveSecond = nowInstance.minusMillis(5000L)
         Timestamp.from(nowInstance) shouldBeAfter Timestamp.from(instanceBeforeFiveSecond)
      }

      "timestamp of current instance should not be after the timestamp of future instance" {
         val nowInstance = Instant.now()
         val instanceAfterFiveSecond = nowInstance.plusMillis(5000L)
         Timestamp.from(nowInstance) shouldNotBeAfter Timestamp.from(instanceAfterFiveSecond)
      }

      "timestamp of current instance should not be after the another timestamp of same instance" {
         val nowInstance = Instant.now()
         Timestamp.from(nowInstance) shouldNotBeAfter Timestamp.from(nowInstance)
      }

      "timestamp of current instance should be before the timestamp of future instance" {
         val nowInstance = Instant.now()
         val instanceAfterFiveSecond = nowInstance.plusMillis(5000L)
         Timestamp.from(nowInstance) shouldBeBefore Timestamp.from(instanceAfterFiveSecond)
      }

      "timestamp of current instance should not be before the timestamp of past instance" {
         val nowInstance = Instant.now()
         val instanceBeforeFiveSecond = nowInstance.minusMillis(5000L)
         Timestamp.from(nowInstance) shouldNotBeBefore Timestamp.from(instanceBeforeFiveSecond)
      }

      "timestamp of current instance should not be before the another timestamp of same instance" {
         val nowInstance = Instant.now()
         Timestamp.from(nowInstance) shouldNotBeBefore Timestamp.from(nowInstance)
      }
   }
}
