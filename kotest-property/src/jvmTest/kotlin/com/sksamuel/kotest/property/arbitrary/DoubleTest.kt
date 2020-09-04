package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.sequences.shouldHaveAtLeastSize
import io.kotest.property.Arb
import io.kotest.property.arbitrary.numericDoubles
import io.kotest.property.arbitrary.take

class DoubleTest : FunSpec({
   test("Numeric Doubles should generate negative values by default") {
      Arb.numericDoubles()
         .take(10_000)
         .filter { it < 0 }
         .distinct()
         .shouldHaveAtLeastSize(100)
   }
})
