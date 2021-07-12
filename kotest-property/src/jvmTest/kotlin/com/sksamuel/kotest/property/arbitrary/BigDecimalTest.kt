package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.concurrent.shouldCompleteWithin
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bigDecimal
import io.kotest.property.arbitrary.take
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

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
})
