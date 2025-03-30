package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.concurrent.shouldCompleteWithin
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bigDecimal
import io.kotest.property.arbitrary.bigDecimalDefaultEdgecases
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.take
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

@EnabledIf(NotMacOnGithubCondition::class)
class BigDecimalTest : FunSpec({

   test("Arb.bigDecimal(min, max) should generate bigDecimal between given range") {
      val min = BigDecimal.valueOf(123)
      val max = BigDecimal.valueOf(555)

      Arb.bigDecimal(min, max).take(100).forAll {
         (it >= min && it <= max) shouldBe true
      }
   }

   test("Arb.bigDecimal(scale, rounding) should generate bigDecimal of given scale") {
      Arb.bigDecimal(4, RoundingMode.CEILING).take(100).forAll {
         it.scale() shouldBe 4
      }
   }

   test("Arb.bigDecimal(min, max) for large value should complete with in few seconds") {
      shouldCompleteWithin(5, TimeUnit.SECONDS) {
         Arb.bigDecimal(BigDecimal.valueOf(-100_000.00), BigDecimal.valueOf(100_000.00)).take(100).forEach { _ ->
         }
      }
   }

   test("bigDecimalDefaultEdgecases should contain zeros with differing precision") {
      bigDecimalDefaultEdgecases.shouldContain(BigDecimal("0.00"))
      bigDecimalDefaultEdgecases.shouldContain(BigDecimal("0"))
   }

   test("Arb.bigDecimal(min, max) should always contain min as edgecase but not max") {
      val min = BigDecimal.valueOf(123)
      val max = BigDecimal.valueOf(555)

      val actualEdgecases = Arb.bigDecimal(min = min, max = max).edgecases()
      actualEdgecases.shouldContain(min)
      actualEdgecases.shouldNotContain(max)
   }

   test("Arb.bigDecimal(min, max) should only include default edgecases that are in range [min, max)") {
      val min = BigDecimal.valueOf(0)
      val max = BigDecimal.valueOf(5)

      val expectedEdgecases = bigDecimalDefaultEdgecases
         .filter { min <= it && it < max }

      Arb.bigDecimal(min = min, max = max).edgecases().shouldContainAll(expectedEdgecases)
   }

   test("Arb.bigDecimal should generate random terminal digit") {
      fun BigDecimal.lastDigit() = stripTrailingZeros().toString().last().digitToInt()

      Arb.bigDecimal()
         .take(1000)
         .map { it.lastDigit() }
         .toList()
         .shouldContainAll((1..9).toList())
   }


})
