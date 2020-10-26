package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.sequences.shouldHaveAtLeastSize
import io.kotest.property.Arb
import io.kotest.property.arbitrary.numericFloats
import io.kotest.property.arbitrary.take

class FloatTest : FunSpec({
   test("Numeric Float should generate negative values by default") {
      Arb.numericFloats()
         .take(10_000)
         .filter { it < 0 }
         .distinct()
         .shouldHaveAtLeastSize(100)
   }
})
