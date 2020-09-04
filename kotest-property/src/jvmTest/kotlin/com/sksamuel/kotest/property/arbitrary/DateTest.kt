package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.localDate
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.localTime
import io.kotest.property.arbitrary.period
import io.kotest.property.checkAll
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period

class DateTest : WordSpec({

   "Arb.localDate(minYear, maxYear)" should {
      "generate valid LocalDates (no exceptions)" {
         Arb.localDate().generate(RandomSource.Default).take(10_000).toList()
      }

      "generate LocalDates between minYear and maxYear" {
         val years = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()
         val days = mutableSetOf<Int>()

         checkAll(10_000, Arb.localDate(LocalDate.of(1998, 1, 1), LocalDate.of(1999, 12, 31))) {
            years += it.year
            months += it.monthValue
            days += it.dayOfMonth
         }

         years shouldBe setOf(1998, 1999)
         months shouldBe (1..12).toSet()
         days shouldBe (1..31).toSet()
      }

      "Contain Feb 29th if leap year" {
         val leapYear = 2016
         Arb.localDate(LocalDate.of(leapYear, 1, 1), LocalDate.of(leapYear, 12, 31)).edgecases().toList() shouldContain LocalDate.of(2016, 2, 29)
      }

      "Contain the edgecases Feb 28, Jan 01 and Dec 31" {
         Arb.localDate(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 12, 31)).edgecases().toList() shouldContainAll listOf(
            LocalDate.of(2019, 1, 1),
            LocalDate.of(2020, 12, 31)
         )
      }

      "Be the default generator for LocalDate" {
         checkAll(10) { _: LocalDate -> /* No use. Won't reach here if unsupported */ }
      }
   }

   "Arb.localTime()" should {
      "generate N valid LocalTimes(no exceptions)" {
         Arb.localTime().generate(RandomSource.Default).take(10_000).toList()
            .size shouldBe 10_000
      }

      "Be the default generator for LocalTime" {
         checkAll(10) { _: LocalTime -> /* No use. Won't reach here if unsupported */ }
      }
   }

   "Arb.localDateTime(minYear, maxYear)" should {
      "generate N valid LocalDateTimes(no exceptions)" {
         Arb.localDateTime().generate(RandomSource.Default).take(10_000).toList()
            .size shouldBe 10_000
      }

      "generate LocalDateTimes between minYear and maxYear" {
         val years = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()
         val days = mutableSetOf<Int>()
         val hours = mutableSetOf<Int>()
         val minutes = mutableSetOf<Int>()
         val seconds = mutableSetOf<Int>()

         checkAll(5000, Arb.localDateTime(1998, 1999)) {
            println(it)
            years += it.year
            months += it.monthValue
            days += it.dayOfMonth
            hours += it.hour
            minutes += it.minute
            seconds += it.second
         }

         years shouldBe setOf(1998, 1999)
         months shouldBe (1..12).toSet()
         days shouldBe (1..31).toSet()
         hours shouldBe (0..23).toSet()
         minutes shouldBe (0..59).toSet()
      }

      "Be the default generator for LocalDateTime" {
         checkAll(10) { _: LocalDateTime -> /* No use. Won't reach here if unsupported */ }
      }
   }

   "Arb.instant()" should {
      "generate N valid Instants(no exceptions)" {
         Arb.instant().generate(RandomSource.Default).take(10_000).toList().size shouldBe 10_000
      }
   }

   "Gen.period(maxYears)" should {
      "Generate only periods with years <= maxYears" {
         checkAll(10_000, Arb.period(2)) {
            it.years <= 2
         }
      }

      "Generate all possible years in the interval [0, maxYears]" {
         val generated = mutableSetOf<Int>()
         checkAll(10_000, Arb.period(10)) {
            generated += it.years
         }

         generated shouldBe (0..10).toSet()
      }

      "Generate all possible intervals for Months and Days" {
         val generatedDays = mutableSetOf<Int>()
         val generatedMonths = mutableSetOf<Int>()

         checkAll(10_000, Arb.period(10)) {
            generatedDays += it.days
            generatedMonths += it.months
         }

         generatedDays shouldBe (0..31).toSet()
         generatedMonths shouldBe (0..11).toSet()
      }

      "Be the default generator for Duration" {
         checkAll(10) { _: Period -> /* No use. Won't reach here if unsupported */ }
      }
   }
})
