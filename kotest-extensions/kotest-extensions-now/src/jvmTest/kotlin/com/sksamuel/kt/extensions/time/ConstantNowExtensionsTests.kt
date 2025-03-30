package com.sksamuel.kt.extensions.time

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.time.withConstantNow
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlinx.coroutines.delay
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.chrono.HijrahDate
import java.time.chrono.JapaneseDate
import java.time.chrono.MinguoDate
import java.time.chrono.ThaiBuddhistDate

@EnabledIf(NotMacOnGithubCondition::class)
class ConstantNowExtensionFunctionsTest : DescribeSpec() {

   private val zoneId = ZoneId.of("Europe/Paris")
   private val clock = Clock.systemUTC()

   init {
      describe("The ConstantNow extension function (HijrahDate)") {

         val now = HijrahDate.now()

         it("Should replace the HijrahDate.now() with my own dateTime") {
            withConstantNow(now) {
               HijrahDate.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the HirahData.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               HijrahDate.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the HirahData.now(clock) to my own dateTime") {
            withConstantNow(now) {
               HijrahDate.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            HijrahDate.now() shouldNotBeSameInstanceAs now
         }
      }
      describe("The ConstantNow extension function (Instant)") {

         val now = Instant.now()

         it("Should replace the Instant.now() with my own dateTime") {
            withConstantNow(now) {
               Instant.now() shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            Instant.now() shouldNotBeSameInstanceAs now
         }


         it("Should replace the Instant.now(clock) to my own dateTime") {
            withConstantNow(now) {
               Instant.now(clock) shouldBeSameInstanceAs now
            }
         }
      }
      describe("The ConstantNow extension function (JapaneseDate)") {

         val now = JapaneseDate.now()

         it("Should replace the JapaneseDate.now() with my own dateTime") {
            withConstantNow(now) {
               JapaneseDate.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the JapaneseDate.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               JapaneseDate.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the JapaneseDate.now(clock) to my own dateTime") {
            withConstantNow(now) {
               JapaneseDate.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            JapaneseDate.now() shouldNotBeSameInstanceAs now
         }
      }
      describe("The ConstantNow extension function (LocalDate)") {

         val now = LocalDate.now()

         it("Should replace the LocalDate.now() with my own dateTime") {
            withConstantNow(now) {
               LocalDate.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the LocalDate.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               LocalDate.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the LocalDate.now(clock) to my own dateTime") {
            withConstantNow(now) {
               LocalDate.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            LocalDate.now() shouldNotBeSameInstanceAs now
         }
      }
      describe("The ConstantNow extension function (LocalDateTime)") {

         val now = LocalDateTime.now()

         it("Should replace the LocalDateTime.now() with my own dateTime") {
            withConstantNow(now) {
               LocalDateTime.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the LocalDateTime.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               LocalDateTime.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the LocalDateTime.now(clock) to my own dateTime") {
            withConstantNow(now) {
               LocalDateTime.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            LocalDateTime.now() shouldNotBeSameInstanceAs now
         }
      }
      describe("The ConstantNow extension function (LocalTime)") {

         val now = LocalTime.now()

         it("Should replace the LocalTime.now() with my own dateTime") {
            withConstantNow(now) {
               LocalTime.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the LocalTime.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               LocalTime.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the LocalTime.now(clock) to my own dateTime") {
            withConstantNow(now) {
               LocalTime.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            LocalTime.now() shouldNotBeSameInstanceAs now
         }
      }
      describe("The ConstantNow extension function (MinguoDate)") {

         val now = MinguoDate.now()

         it("Should replace the MinguoDate.now() with my own dateTime") {
            withConstantNow(now) {
               MinguoDate.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the MinguoDate.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               MinguoDate.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the MinguoDate.now(clock) to my own dateTime") {
            withConstantNow(now) {
               MinguoDate.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            MinguoDate.now() shouldNotBeSameInstanceAs now
         }
      }
      describe("The ConstantNow extension function (OffsetDateTime)") {

         val now = OffsetDateTime.now()

         it("Should replace the OffsetDateTime.now() with my own dateTime") {
            withConstantNow(now) {
               OffsetDateTime.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the OffsetDateTime.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               OffsetDateTime.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the OffsetDateTime.now(clock) to my own dateTime") {
            withConstantNow(now) {
               OffsetDateTime.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            OffsetDateTime.now() shouldNotBeSameInstanceAs now
         }
      }
      describe("The ConstantNow extension function (OffsetTime)") {

         val now = OffsetTime.now()

         it("Should replace the OffsetTime.now() with my own dateTime") {
            withConstantNow(now) {
               OffsetTime.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the OffsetTime.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               OffsetTime.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the OffsetTime.now(clock) to my own dateTime") {
            withConstantNow(now) {
               OffsetTime.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            OffsetTime.now() shouldNotBeSameInstanceAs now
         }
      }
      describe("The ConstantNow extension function (ThaiBuddhistDate)") {

         val now = ThaiBuddhistDate.now()

         it("Should replace the ThaiBuddhistDate.now() with my own dateTime") {
            withConstantNow(now) {
               ThaiBuddhistDate.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the ThaiBuddhistDate.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               ThaiBuddhistDate.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the ThaiBuddhistDate.now(clock) to my own dateTime") {
            withConstantNow(now) {
               ThaiBuddhistDate.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            ThaiBuddhistDate.now() shouldNotBeSameInstanceAs now
         }
      }
      describe("The ConstantNow extension function (Year)") {

         val now = Year.now()

         it("Should replace the Year.now() with my own dateTime") {
            withConstantNow(now) {
               Year.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the Year.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               Year.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the Year.now(clock) to my own dateTime") {
            withConstantNow(now) {
               Year.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            Year.now() shouldNotBeSameInstanceAs now
         }
      }
      describe("The ConstantNow extension function (YearMonth)") {

         val now = YearMonth.now()

         it("Should replace the YearMonth.now() with my own dateTime") {
            withConstantNow(now) {
               YearMonth.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the YearMonth.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               YearMonth.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the YearMonth.now(clock) to my own dateTime") {
            withConstantNow(now) {
               YearMonth.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            YearMonth.now() shouldNotBeSameInstanceAs now
         }
      }
      describe("The ConstantNow extension function (ZonedDateTime)") {

         val now = ZonedDateTime.now()

         it("Should replace the ZonedDateTime.now() with my own dateTime") {
            withConstantNow(now) {
               ZonedDateTime.now() shouldBeSameInstanceAs now
            }
         }

         it("Should replace the ZonedDateTime.now(zoneId) to my own dateTime") {
            withConstantNow(now) {
               ZonedDateTime.now(zoneId) shouldBeSameInstanceAs now
            }
         }

         it("Should replace the ZonedDateTime.now(clock) to my own dateTime") {
            withConstantNow(now) {
               ZonedDateTime.now(clock) shouldBeSameInstanceAs now
            }
         }

         it("Should reverse to default behavior after execution") {
            withConstantNow(now) { }
            delay(10)

            ZonedDateTime.now() shouldNotBeSameInstanceAs now
         }
      }
   }
}
