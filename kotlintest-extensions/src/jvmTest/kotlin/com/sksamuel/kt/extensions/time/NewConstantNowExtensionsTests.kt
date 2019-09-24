package com.sksamuel.kt.extensions.time

import io.kotlintest.extensions.time.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.DescribeSpec
import kotlinx.coroutines.delay
import java.time.*
import java.time.chrono.HijrahDate
import java.time.chrono.JapaneseDate
import java.time.chrono.MinguoDate
import java.time.chrono.ThaiBuddhistDate

class NewConstantNowExtensionsTests : DescribeSpec() {

   init {
      describe("The ConstantNow extension function (HijrahDate)") {

         val now = ZonedDateTime.now()

         it("Should replace the HijrahDate.now() with my own dateTime") {
            newWithConstantNow(now) {
               HijrahDate.now() shouldBe now.toHijrahDate()
               HijrahDate.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toHijrahDate()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            HijrahDate.now() shouldNotBe now.toHijrahDate()
            HijrahDate.now(zoneId) shouldNotBe now.withZoneSameInstant(zoneId).toHijrahDate()
         }
      }
      describe("The ConstantNow extension function (Instant)") {
         val now = ZonedDateTime.now()


         it("Should replace the Instant.now() with my own dateTime") {
            newWithConstantNow(now) {
               Instant.now() shouldBe now.toInstant()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            Instant.now() shouldNotBe now.toInstant()
         }
      }
      describe("The ConstantNow extension function (JapaneseDate)") {

         val now = ZonedDateTime.now()

         it("Should replace the JapaneseDate.now() with my own dateTime") {
            newWithConstantNow(now) {
               JapaneseDate.now() shouldBe now.toJapaneseDate()
               JapaneseDate.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toJapaneseDate()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            JapaneseDate.now() shouldNotBe now.toJapaneseDate()
            JapaneseDate.now(zoneId) shouldNotBe now.withZoneSameInstant(zoneId).toJapaneseDate()
         }
      }
      describe("The ConstantNow extension function (LocalDate)") {

         val now = ZonedDateTime.now()

         it("Should replace the LocalDate.now() with my own dateTime") {
            newWithConstantNow(now) {
               LocalDate.now() shouldBe now.toLocalDate()
               LocalDate.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toLocalDate()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            LocalDate.now() shouldNotBe now.toLocalDate()
            LocalDate.now(zoneId) shouldNotBe now.withZoneSameInstant(zoneId).toLocalDate()
         }
      }
      describe("The ConstantNow extension function (LocalDateTime)") {

         val now = ZonedDateTime.now()

         it("Should convert the LocalDateTime.now() to my own dateTime in corresponding zoneId") {
            newWithConstantNow(now) {
               LocalDateTime.now() shouldBe now.toLocalDateTime()
               LocalDateTime.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toLocalDateTime()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            LocalDateTime.now() shouldNotBe now.toLocalDateTime()
            LocalDateTime.now(zoneId) shouldNotBe now.withZoneSameInstant(zoneId).toLocalDateTime()

         }
      }
      describe("The ConstantNow extension function (LocalTime)") {

         val now = ZonedDateTime.now()

         it("Should replace the LocalTime.now() with my own dateTime") {
            newWithConstantNow(now) {
               LocalTime.now() shouldBe now.toLocalTime()
               LocalTime.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toLocalTime()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            LocalTime.now() shouldNotBe now.toLocalTime()
            LocalTime.now(zoneId) shouldNotBe now.withZoneSameInstant(zoneId).toLocalTime()
         }
      }
      describe("The ConstantNow extension function (MinguoDate)") {
         val now = ZonedDateTime.now()

         it("Should replace the MinguoDate.now() with my own dateTime") {
            newWithConstantNow(now) {
               MinguoDate.now() shouldBe now.toMinguoDate()
               MinguoDate.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toMinguoDate()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            MinguoDate.now() shouldNotBe now.toMinguoDate()
            MinguoDate.now(zoneId) shouldNotBe now.withZoneSameInstant(zoneId).toMinguoDate()

         }
      }
      describe("The ConstantNow extension function (OffsetDateTime)") {
         val now = ZonedDateTime.now()

         it("Should replace the OffsetDateTime.now() with my own dateTime") {
            newWithConstantNow(now) {
               OffsetDateTime.now() shouldBe now.toOffsetDateTime()
            }
         }

         it("Should convert the OffsetDateTime.now(zoneId) to my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
               OffsetDateTime.now(zoneId) shouldBe OffsetDateTime.ofInstant(now.toInstant(), zoneId)
               OffsetDateTime.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toOffsetDateTime()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            OffsetDateTime.now() shouldNotBe now.toOffsetDateTime()
            OffsetDateTime.now(zoneId) shouldNotBe OffsetDateTime.ofInstant(now.toInstant(), zoneId)
         }
      }
      describe("The ConstantNow extension function (OffsetTime)") {

         val now = ZonedDateTime.now()

         it("Should replace the OffsetTime.now() with my own dateTime") {
            newWithConstantNow(now) {
               OffsetTime.now() shouldBe  now.toOffsetTime()
               OffsetTime.now(zoneId) shouldBe  now.withZoneSameInstant(zoneId).toOffsetTime()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            OffsetTime.now() shouldNotBe now.toOffsetTime()
            OffsetTime.now(zoneId) shouldNotBe now.withZoneSameInstant(zoneId).toOffsetTime()
         }
      }
      describe("The ConstantNow extension function (ThaiBuddhistDate)") {

         val now = ZonedDateTime.now()

         it("Should replace the ThaiBuddhistDate.now() with my own dateTime") {
            newWithConstantNow(now) {
               ThaiBuddhistDate.now() shouldBe now.toThaiBuddhistDate()
               ThaiBuddhistDate.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toThaiBuddhistDate()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            ThaiBuddhistDate.now() shouldNotBe now.toThaiBuddhistDate()
            ThaiBuddhistDate.now(zoneId) shouldNotBe now.withZoneSameInstant(zoneId).toThaiBuddhistDate()
         }
      }
      describe("The ConstantNow extension function (Year)") {

         val now = ZonedDateTime.now()

         it("Should replace the Year.now() with my own dateTime") {
            newWithConstantNow(now) {
               Year.now() shouldBe now.toYear()
               Year.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toYear()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            Year.now() shouldNotBe now.toYear()
            Year.now(zoneId) shouldNotBe now.withZoneSameInstant(zoneId).toYear()
         }
      }
      describe("The ConstantNow extension function (YearMonth)") {

         val now = ZonedDateTime.now()

         it("Should replace the YearMonth.now() with my own dateTime") {
            newWithConstantNow(now) {
               YearMonth.now() shouldBe now.toYearMonth()
               YearMonth.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toYearMonth()
            }
         }

         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(31L * 24L * 60L * 60L * 1000L)

            YearMonth.now() shouldNotBe now.toYearMonth()
            YearMonth.now(zoneId) shouldNotBe now.withZoneSameInstant(zoneId).toYearMonth()

         }
      }
      describe("The ConstantNow extension function (ZonedDateTime)") {
         val now = ZonedDateTime.now()

         it("Should replace the ZonedDateTime.now() with my own dateTime") {
            newWithConstantNow(now) {
               ZonedDateTime.now() shouldBe now
            }
         }

         it("Should convert the ZonedDateTime.now(zoneId) to my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
               ZonedDateTime.now(zoneId) shouldNotBe now.withZoneSameInstant(zoneId)
            }
         }


         it("Should reverse to default behavior after execution") {
            newWithConstantNow(now) { }
            delay(10)

            ZonedDateTime.now() shouldNotBe now
            ZonedDateTime.now(zoneId) shouldNotBe ZonedDateTime.ofInstant(now.toInstant(), zoneId)
         }
      }
   }
}

private val zoneId = ZoneId.of("Europe/Paris")
