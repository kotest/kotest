package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.sequences.shouldHaveAtLeastSize
import io.kotest.property.Arb
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.negativeFloat
import io.kotest.property.arbitrary.numericFloat
import io.kotest.property.arbitrary.positiveFloat
import io.kotest.property.arbitrary.take

@EnabledIf(LinuxOnlyGithubCondition::class)
class FloatTest : FunSpec({
   test("Numeric Float should generate negative values by default") {
      Arb.numericFloat()
         .take(10_000)
         .filter { it < 0 }
         .distinct()
         .shouldHaveAtLeastSize(100)
   }

   test("negativeFloat should include valid negative numeric float edgecases") {
      Arb.negativeFloat(includeNaNs = false).edgecases(1000) shouldContainExactly setOf(
         -1.0F,
         -Float.MIN_VALUE,
         -Float.MAX_VALUE,
      )
   }

   test("positiveFloat should include valid positive numeric float edgecases") {
      Arb.positiveFloat(includeNaNs = false).edgecases(1000) shouldContainExactly setOf(
         1.0F,
         Float.MIN_VALUE,
         Float.MAX_VALUE,
      )
   }

   test("float should include numeric float edgecases") {
      Arb.float(includeNaNs = false).edgecases(1000) shouldContainExactly setOf(
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

   test("Infinity edgecases should only be generated when the range includes them") {
      Arb.float(Float.NEGATIVE_INFINITY..Float.POSITIVE_INFINITY).edgecases(1000).toList()
         .shouldContainAll(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY)

      Arb.float(-Float.MAX_VALUE..Float.POSITIVE_INFINITY).edgecases(1000).toList()
         .shouldContain(Float.POSITIVE_INFINITY)
         .shouldNotContain(Float.NEGATIVE_INFINITY)

      Arb.float(Float.NEGATIVE_INFINITY..Float.MAX_VALUE).edgecases(1000).toList()
         .shouldContain(Float.NEGATIVE_INFINITY)
         .shouldNotContain(Float.POSITIVE_INFINITY)
   }

   test("NaNs should be included regardless of range when includeNaNs is true") {
      Arb.float(0F..100F, includeNaNs = true).edgecases(1000).toList().any { it.isNaN() }
   }

   test("NaNs should not be included regardless of range when includeNaNs is false") {
      Arb.float(0F..100F, includeNaNs = false).edgecases(1000).toList().none { it.isNaN() }
   }
})
