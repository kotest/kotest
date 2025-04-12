package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.sequences.shouldHaveAtLeastSize
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.negativeDouble
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.arbitrary.positiveDouble
import io.kotest.property.arbitrary.take

@EnabledIf(LinuxOnlyGithubCondition::class)
class DoubleTest : FunSpec({
   test("Numeric Doubles should generate negative values by default") {
      Arb.numericDouble()
         .take(10_000)
         .filter { it < 0 }
         .distinct()
         .shouldHaveAtLeastSize(100)
   }

   test("negativeDouble should include valid negative numeric double edgecases") {
      Arb.negativeDouble(includeNonFiniteEdgeCases = false).edgecases(1000) shouldContainExactly setOf(
         -1.0,
         -Double.MIN_VALUE,
         -Double.MAX_VALUE,
      )
   }

   test("negativeDouble should include valid negative all double edgecases when includeNonFiniteEdgeCases is true") {
      Arb.negativeDouble().edgecases(1000) shouldContainExactly setOf(
         -1.0,
         Double.NEGATIVE_INFINITY,
         -Double.MIN_VALUE,
         -Double.MAX_VALUE,
      )
   }

   test("positiveDouble should include valid positive numeric double edgecases") {
      Arb.positiveDouble(includeNonFiniteEdgeCases = false).edgecases(1000) shouldContainExactly setOf(
         1.0,
         Double.MIN_VALUE,
         Double.MAX_VALUE,
      )
   }

   test("posititveDouble should include valid positive all double edgecases when includeNonFiniteEdgeCases is true") {
      Arb.positiveDouble().edgecases(1000) shouldContainExactly setOf(
         1.0,
         Double.MIN_VALUE,
         Double.MAX_VALUE,
         Double.POSITIVE_INFINITY,
      )
   }

   test("double should include numeric double edgecases") {
      Arb.double(includeNonFiniteEdgeCases = false).edgecases(1000) shouldContainExactly setOf(
         0.0,
         -0.0,
         -1.0,
         -Double.MIN_VALUE,
         -Double.MAX_VALUE,
         1.0,
         Double.MIN_VALUE,
         Double.MAX_VALUE,
      )
   }

   test("double should include all double edgecases when includeNonFiniteEdgeCases is true") {
      Arb.double().edgecases(1000) shouldContainExactly setOf(
         0.0,
         -0.0,
         -1.0,
         -Double.MIN_VALUE,
         -Double.MAX_VALUE,
         1.0,
         Double.MIN_VALUE,
         Double.MAX_VALUE,
         Double.POSITIVE_INFINITY,
         Double.NEGATIVE_INFINITY,
         Double.NaN
      )
   }
})
