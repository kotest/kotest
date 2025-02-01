package io.kotest.property.kotlinx.datetime

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.property.Arb
import io.kotest.property.forAll

class DateTest : FunSpec() {
   init {
      test("Arb.date should generate day spread") {
         Arb.date().samples().take(1000000).map { it.value.dayOfYear }.toSet().shouldHaveSize(365)
      }
      test("Arb.date should respect year range") {
          forAll(Arb.date(1980..1988)) { date ->
              date.year in 1980..1988
          }
      }
   }
}
