package com.sksamuel.kt.extensions.time

import io.kotest.extensions.time.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotlintest.specs.DescribeSpec
import kotlinx.coroutines.delay
import java.time.*
import java.time.chrono.HijrahDate
import java.time.chrono.JapaneseDate
import java.time.chrono.MinguoDate
import java.time.chrono.ThaiBuddhistDate

class NewConstantNowExtensionsTests : DescribeSpec() {
   
   private val now = ZonedDateTime.now()

   init {
      describe("The ConstantNow extension function (HijrahDate)") {

         it("Should replace the HijrahDate.now() with HijrahDate format of my own dateTime") {
            newWithConstantNow(now) {
               HijrahDate.now() shouldBe now.toHijrahDate()
            }
         }

         it("Should replace the HijrahDate.now(zoneId) with HijrahDate format of my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
               HijrahDate.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toHijrahDate()
            }
         }

      }

      describe("The ConstantNow extension function (Instant)") {

         it("Should replace the Instant.now() with Instant format of my own dateTime") {
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

         it("Should replace the JapaneseDate.now() with JapaneseDate format of my own dateTime") {
            newWithConstantNow(now) {
               JapaneseDate.now() shouldBe now.toJapaneseDate()
            }
         }

         it("Should replace the JapaneseDate.now(zoneId) with JapaneseDate format of my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
               JapaneseDate.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toJapaneseDate()
            }
         }
      }
      
      describe("The ConstantNow extension function (LocalDate)") {

         it("Should replace the LocalDate.now() with LocalDate format of my own dateTime") {
            newWithConstantNow(now) {
               LocalDate.now() shouldBe now.toLocalDate()
            }
         }

         it("Should replace the LocalDate.now(zoneId) with LocalDate format of my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
               LocalDate.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toLocalDate()
            }
         }
      }
      
      describe("The ConstantNow extension function (LocalDateTime)") {

         it("Should convert the LocalDateTime.now() to LocalDateTime format of my own dateTime in corresponding zoneId") {
            newWithConstantNow(now) {
               LocalDateTime.now() shouldBe now.toLocalDateTime()
            }
         }

         it("Should replace the LocalDateTime.now(zoneId) with LocalDateTime format my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
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

         it("Should replace the LocalTime.now() with LocalTime format of my own dateTime") {
            newWithConstantNow(now) {
               LocalTime.now() shouldBe now.toLocalTime()
            }
         }

         it("Should replace the LocalTime.now(zoneId) with LocalTime format my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
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

         it("Should replace the MinguoDate.now() with MinguoDate format of my own dateTime") {
            newWithConstantNow(now) {
               MinguoDate.now() shouldBe now.toMinguoDate()
            }
         }

         it("Should replace the MinguoDate.now(zoneId) with MinguoDate format my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
               MinguoDate.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toMinguoDate()
            }
         }
      }
      
      describe("The ConstantNow extension function (OffsetDateTime)") {

         it("Should replace the OffsetDateTime.now() with OffsetDateTime format of my own dateTime") {
            newWithConstantNow(now) {
               OffsetDateTime.now() shouldBe now.toOffsetDateTime()
            }
         }

         it("Should replace the OffsetDateTime.now(zoneId) with OffsetDateTime format of my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
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

         it("Should replace the OffsetTime.now() with OffsetTime format of my own dateTime") {
            newWithConstantNow(now) {
               OffsetTime.now() shouldBe  now.toOffsetTime()
            }
         }

         it("Should replace the OffsetTime.now(zoneId) with OffsetTime format my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
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

         it("Should replace the ThaiBuddhistDate.now() with ThaiBuddhistDate format of my own dateTime") {
            newWithConstantNow(now) {
               ThaiBuddhistDate.now() shouldBe now.toThaiBuddhistDate()
            }
         }

         it("Should replace the ThaiBuddhistDate.now(zoneId) with ThaiBuddhistDate format my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
               ThaiBuddhistDate.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toThaiBuddhistDate()
            }
         }
      }
      
      describe("The ConstantNow extension function (Year)") {

         it("Should replace the Year.now() with Year format of my own dateTime") {
            newWithConstantNow(now) {
               Year.now() shouldBe now.toYear()
            }
         }

         it("Should replace the Year.now(zoneId) with Year format my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
               Year.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toYear()
            }
         }
      }
      
      describe("The ConstantNow extension function (YearMonth)") {

         it("Should replace the YearMonth.now() with YearMonth format of my own dateTime") {
            newWithConstantNow(now) {
               YearMonth.now() shouldBe now.toYearMonth()
            }
         }

         it("Should replace the YearMonth.now(zoneId) with YearMonth format my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
               YearMonth.now(zoneId) shouldBe now.withZoneSameInstant(zoneId).toYearMonth()
            }
         }
      }
      
      describe("The ConstantNow extension function (ZonedDateTime)") {

         it("Should replace the ZonedDateTime.now() with my own dateTime") {
            newWithConstantNow(now) {
               ZonedDateTime.now() shouldBe now
            }
         }

         it("Should replace the ZonedDateTime.now(zoneId) with my own dateTime in corresponding zone") {
            newWithConstantNow(now) {
               ZonedDateTime.now(zoneId) shouldBe now.withZoneSameInstant(zoneId)
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
