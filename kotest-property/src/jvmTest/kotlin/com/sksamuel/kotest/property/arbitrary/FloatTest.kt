package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.sequences.shouldHaveAtLeastSize
import io.kotest.property.Arb
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.negativeFloat
import io.kotest.property.arbitrary.numericFloat
import io.kotest.property.arbitrary.positiveFloat
import io.kotest.property.arbitrary.take

class FloatTest : FunSpec({
   test("Numeric Float should generate negative values by default") {
      Arb.numericFloat()
         .take(10_000)
         .filter { it < 0 }
         .distinct()
         .shouldHaveAtLeastSize(100)
   }

   test("negativeFloat should include valid negative float edgecases") {
      Arb.negativeFloat().edgecases(1000) shouldContainExactly setOf(
         -1.0F,
         Float.NEGATIVE_INFINITY,
         -Float.MIN_VALUE,
         -Float.MAX_VALUE,
      )
   }

   test("positiveFloat should include valid positive float edgecases") {
      Arb.positiveFloat().edgecases(1000) shouldContainExactly setOf(
         1.0F,
         Float.POSITIVE_INFINITY,
         Float.MIN_VALUE,
         Float.MAX_VALUE,
      )
   }

   test("float should include float edgecases") {
      Arb.float().edgecases(1000) shouldContainExactly setOf(
         0.0F,
         -0.0F,
         Float.NaN,
         -1.0F,
         Float.NEGATIVE_INFINITY,
         -Float.MIN_VALUE,
         -Float.MAX_VALUE,
         1.0F,
         Float.POSITIVE_INFINITY,
         Float.MIN_VALUE,
         Float.MAX_VALUE,
      )
   }
})
