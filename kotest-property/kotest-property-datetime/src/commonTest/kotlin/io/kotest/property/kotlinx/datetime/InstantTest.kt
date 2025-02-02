package io.kotest.property.kotlinx.datetime

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.forAll

class InstantTest : FunSpec() {
   init {
      test("Arb.instant should respect range") {
          forAll(Arb.instant(10000L..20000L)) {
              it.toEpochMilliseconds() in 10000..20000
          }
      }
   }
}
