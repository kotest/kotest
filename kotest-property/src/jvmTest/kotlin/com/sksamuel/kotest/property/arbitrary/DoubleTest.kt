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
      Arb.negativeDouble(includeNaNs = false).edgecases(1000) shouldContainExactly setOf(
         -1.0,
         -Double.MIN_VALUE,
         -Double.MAX_VALUE,
      )
   }

   test("positiveDouble should include valid positive numeric double edgecases") {
      Arb.positiveDouble(includeNaNs = false).edgecases(1000) shouldContainExactly setOf(
         1.0,
         Double.MIN_VALUE,
         Double.MAX_VALUE,
      )
   }

   test("double should include numeric double edgecases") {
      Arb.double(includeNaNs = false).edgecases(1000) shouldContainExactly setOf(
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

   test("Infinity edgecases should only be generated when the range includes them") {
      Arb.double(Double.NEGATIVE_INFINITY..Double.POSITIVE_INFINITY).edgecases(1000).toList()
         .shouldContainAll(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)

      Arb.double(-Double.MAX_VALUE..Double.POSITIVE_INFINITY).edgecases(1000).toList()
         .shouldContain(Double.POSITIVE_INFINITY)
         .shouldNotContain(Double.NEGATIVE_INFINITY)

      Arb.double(Double.NEGATIVE_INFINITY..Double.MAX_VALUE).edgecases(1000).toList()
         .shouldContain(Double.NEGATIVE_INFINITY)
         .shouldNotContain(Double.POSITIVE_INFINITY)
   }

   test("NaNs should be included regardless of range when includeNaNs is true") {
      Arb.double(0.0..100.0, includeNaNs = true).edgecases(1000).toList().any { it.isNaN() }
   }

   test("NaNs should not be included regardless of range when includeNaNs is false") {
      Arb.double(0.0..100.0, includeNaNs = false).edgecases(1000).toList().none { it.isNaN() }
   }
})
