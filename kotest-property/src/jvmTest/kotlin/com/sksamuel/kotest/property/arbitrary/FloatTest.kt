package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
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

@EnabledIf(NotMacOnGithubCondition::class)
class FloatTest : FunSpec({
   test("Numeric Float should generate negative values by default") {
      Arb.numericFloat()
         .take(10_000)
         .filter { it < 0 }
         .distinct()
         .shouldHaveAtLeastSize(100)
   }

   test("negativeFloat should include valid negative numeric float edgecases") {
      Arb.negativeFloat(includeNonFiniteEdgeCases = false).edgecases(1000) shouldContainExactly setOf(
         -1.0F,
         -Float.MIN_VALUE,
         -Float.MAX_VALUE,
      )
   }

   test("negativeFloat should include valid negative all float edgecases when includeNonFiniteEdgeCases is true") {
      Arb.negativeFloat().edgecases(1000) shouldContainExactly setOf(
         -1.0F,
         Float.NEGATIVE_INFINITY,
         -Float.MIN_VALUE,
         -Float.MAX_VALUE,
      )
   }

   test("positiveFloat should include valid positive numeric float edgecases") {
      Arb.positiveFloat(includeNonFiniteEdgeCases = false).edgecases(1000) shouldContainExactly setOf(
         1.0F,
         Float.MIN_VALUE,
         Float.MAX_VALUE,
      )
   }

   test("posititveFloat should include valid positive all float edgecases when includeNonFiniteEdgeCases is true") {
      Arb.positiveFloat().edgecases(1000) shouldContainExactly setOf(
         1.0F,
         Float.MIN_VALUE,
         Float.MAX_VALUE,
         Float.POSITIVE_INFINITY,
      )
   }

   test("float should include numeric float edgecases") {
      Arb.float(includeNonFiniteEdgeCases = false).edgecases(1000) shouldContainExactly setOf(
         0.0F,
         -0.0F,
         -1.0F,
         -Float.MIN_VALUE,
         -Float.MAX_VALUE,
         1.0F,
         Float.MIN_VALUE,
         Float.MAX_VALUE,
      )
   }

   test("float should include all float edgecases when includeNonFiniteEdgeCases is true") {
      Arb.float().edgecases(1000) shouldContainExactly setOf(
         0.0F,
         -0.0F,
         -1.0F,
         -Float.MIN_VALUE,
         -Float.MAX_VALUE,
         1.0F,
         Float.MIN_VALUE,
         Float.MAX_VALUE,
         Float.POSITIVE_INFINITY,
         Float.NEGATIVE_INFINITY,
         Float.NaN
      )
   }
})
