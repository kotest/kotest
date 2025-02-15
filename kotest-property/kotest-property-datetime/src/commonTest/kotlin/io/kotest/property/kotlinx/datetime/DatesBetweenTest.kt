package io.kotest.property.kotlinx.datetime

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import kotlinx.datetime.LocalDate

class DatesBetweenTest: StringSpec() {
   init {
      "should generate dates in the given range" {
         val startDate = LocalDate(2020, 1, 1)
         val endDate = LocalDate(2020, 12, 31)
         io.kotest.property.forAll(
            Arb.Companion.datesBetween(startDate, endDate),
         ) { date ->
            date in startDate..endDate
         }
      }
      "should generate all dates in range" {
         val startDate = LocalDate(2020, 1, 1)
         val endDate = LocalDate(2020, 1, 11)
         Arb.Companion.datesBetween(startDate, endDate)
            .samples()
            .take(100_000)
            .map { it.value }
            .toSet()
            .size shouldBe 11
      }
   }
}
