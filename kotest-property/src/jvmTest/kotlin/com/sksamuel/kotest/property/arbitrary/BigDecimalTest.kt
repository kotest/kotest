package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bigDecimal
import io.kotest.property.arbitrary.take
import io.kotest.property.checkAll
import java.math.BigDecimal
import java.math.RoundingMode

class BigDecimalTest : FunSpec({
   test("Arb.bigDecimal(min, max) should generate bigDecimal between given range") {
      val min = BigDecimal.valueOf(Double.MIN_VALUE)
      val max = BigDecimal.valueOf(Double.MAX_VALUE)

      Arb.bigDecimal(min, max).take(1_00_000).forAll {
         (it >= min && it <= max) shouldBe true
      }
   }

   test("Arb.bigDecimal(scale, rounding) should generate bigDecimal of given scale") {
      Arb.bigDecimal(4, RoundingMode.CEILING).take(1_00_000).forAll {
         assertSoftly {
            it.scale() shouldBe 4
         }
      }
   }
})
