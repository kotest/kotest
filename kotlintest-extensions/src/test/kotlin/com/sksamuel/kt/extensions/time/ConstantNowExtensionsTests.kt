package com.sksamuel.kt.extensions.time

import io.kotlintest.*
import io.kotlintest.extensions.time.ConstantNowTestListener
import io.kotlintest.extensions.time.withConstantNow
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.matchers.types.shouldNotBeSameInstanceAs
import io.kotlintest.specs.DescribeSpec
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.delay
import java.time.*
import java.time.chrono.HijrahDate
import java.time.chrono.JapaneseDate
import java.time.chrono.MinguoDate
import java.time.chrono.ThaiBuddhistDate

class ConstantNowExtensionFunctionsTest : DescribeSpec() {

  init {
    describe("The ConstantNow extension function (HijrahDate)") {
    
      val now = HijrahDate.now()
    
      it("Should replace the HijrahDate.now() with my own dateTime") {
        withConstantNow(now) {
          HijrahDate.now() shouldBeSameInstanceAs now
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
    }
    describe("The ConstantNow extension function (JapaneseDate)") {
    
      val now = JapaneseDate.now()
    
      it("Should replace the JapaneseDate.now() with my own dateTime") {
        withConstantNow(now) {
          JapaneseDate.now() shouldBeSameInstanceAs now
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
    
      it("Should reverse to default behavior after execution") {
        withConstantNow(now) { }
        delay(10)
      
        LocalDate.now() shouldNotBeSameInstanceAs now
      }
    }
    describe("The ConstantNow extension function (LocalDateTime)") {

      val now = LocalDateTime.now()
      val zoneOffset = Clock.systemDefaultZone().zone.rules.getOffset(now)
      val zoneId = ZoneId.of("Europe/Paris")

      it("Should convert the LocalDateTime.now() to my own dateTime in corresponding zoneId") {
        withConstantNow(now) {
          LocalDateTime.now() shouldBeSameInstanceAs now
          LocalDateTime.now(zoneId) shouldBe LocalDateTime.ofInstant(now.toInstant(zoneOffset), zoneId)
        }
      }
    
      it("Should reverse to default behavior after execution") {
        withConstantNow(now) { }
        delay(10)
      
        LocalDateTime.now() shouldNotBeSameInstanceAs now
        LocalDateTime.now(zoneId) shouldNotBe LocalDateTime.ofInstant(now.toInstant(zoneOffset), zoneId)

      }
    }
    describe("The ConstantNow extension function (LocalTime)") {
    
      val now = LocalTime.now()
    
      it("Should replace the LocalTime.now() with my own dateTime") {
        withConstantNow(now) {
          LocalTime.now() shouldBeSameInstanceAs now
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
    
      it("Should reverse to default behavior after execution") {
        withConstantNow(now) { }
        delay(10)
      
        MinguoDate.now() shouldNotBeSameInstanceAs now
      }
    }
    describe("The ConstantNow extension function (OffsetDateTime)") {
    
      val now = OffsetDateTime.now()
      val zoneId = ZoneId.of("Europe/Paris")

      it("Should replace the OffsetDateTime.now() with my own dateTime") {
        withConstantNow(now) {
          OffsetDateTime.now() shouldBeSameInstanceAs now
        }
      }

      it("Should convert the OffsetDateTime.now(zoneId) to my own dateTime in corresponding zone") {
        withConstantNow(now) {
          OffsetDateTime.now(zoneId) shouldBe OffsetDateTime.ofInstant(now.toInstant(), zoneId)
        }
      }

      it("Should reverse to default behavior after execution") {
        withConstantNow(now) { }
        delay(10)
      
        OffsetDateTime.now() shouldNotBeSameInstanceAs now
        OffsetDateTime.now(zoneId) shouldNotBe OffsetDateTime.ofInstant(now.toInstant(), zoneId)
      }
    }
    describe("The ConstantNow extension function (OffsetTime)") {
    
      val now = OffsetTime.now()
    
      it("Should replace the OffsetTime.now() with my own dateTime") {
        withConstantNow(now) {
          OffsetTime.now() shouldBeSameInstanceAs now
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
    
      it("Should reverse to default behavior after execution") {
        withConstantNow(now) { }
        delay(10)
      
        YearMonth.now() shouldNotBeSameInstanceAs now
      }
    }
    describe("The ConstantNow extension function (ZonedDateTime)") {
    
      val now = ZonedDateTime.now()
      val zoneId = ZoneId.of("Europe/Paris")
    
      it("Should replace the ZonedDateTime.now() with my own dateTime") {
        withConstantNow(now) {
          ZonedDateTime.now() shouldBeSameInstanceAs now
        }
      }

      it("Should convert the ZonedDateTime.now(zoneId) to my own dateTime in corresponding zone") {
        withConstantNow(now) {
          ZonedDateTime.now(zoneId) shouldBe ZonedDateTime.ofInstant(now.toInstant(), zoneId)
        }
      }


      it("Should reverse to default behavior after execution") {
        withConstantNow(now) { }
        delay(10)
      
        ZonedDateTime.now() shouldNotBeSameInstanceAs now
        ZonedDateTime.now(zoneId) shouldNotBe ZonedDateTime.ofInstant(now.toInstant(), zoneId)
      }
    }
  }
}

class ConstantNowExtensionsListenerTest : StringSpec() {

  private val myNow = HijrahDate.now()
  private val myNow2 = LocalDateTime.now()
  
  init {
    "Should use my now" {
      HijrahDate.now() shouldBeSameInstanceAs myNow
      LocalDateTime.now() shouldBeSameInstanceAs myNow2
    }
  }
  
  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    HijrahDate.now() shouldNotBeSameInstanceAs myNow
    LocalDateTime.now() shouldNotBeSameInstanceAs myNow2
  }
  
  override fun listeners() = listOf(
          ConstantNowTestListener(myNow),
          ConstantNowTestListener(myNow2)
  )
}