package io.kotest.property.kotlinx.datetime

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.property.Arb
import io.kotest.property.forAll

class DateTimeTest : FunSpec() {
   init {
      test("Arb.datetime should generate day spread") {
         Arb.datetime().samples().take(1000000).map { it.value.dayOfYear }.toSet().shouldHaveSize(365)
      }
      test("Arb.datetime should generate hour spread") {
         Arb.datetime().samples().take(10000).map { it.value.hour }.toSet().shouldHaveSize(24)
      }
      test("Arb.datetime should generate minute spread") {
         Arb.datetime().samples().take(10000).map { it.value.minute }.toSet().shouldHaveSize(60)
      }
      test("Arb.datetime should generate second spread") {
         Arb.datetime().samples().take(10000).map { it.value.second }.toSet().shouldHaveSize(60)
      }
      test("Arb.datetime should respect year range") {
          forAll(Arb.datetime(1980..1988)) { date ->
              date.year in 1980..1988
          }
      }
   }
}
