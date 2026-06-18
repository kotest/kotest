package com.sksamuel.kotest.matchers.bigdecimal

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.bigdecimal.shouldBeEqualIgnoringScale
import io.kotest.matchers.bigdecimal.shouldHaveScale
import io.kotest.matchers.bigdecimal.shouldNotBeEqualIgnoringScale
import io.kotest.matchers.bigdecimal.shouldNotHaveScale
import java.math.BigDecimal

class BigDecimalScaleTest : FunSpec() {
   init {
      test("shouldHaveScale") {
         BigDecimal(10).setScale(3) shouldHaveScale 3
         BigDecimal(10.1) shouldHaveScale 49
         10.444.toBigDecimal() shouldHaveScale 3
         0.toBigDecimal() shouldHaveScale 0
         BigDecimal.ZERO shouldHaveScale 0

         BigDecimal(10).setScale(3) shouldNotHaveScale 1
         BigDecimal(10.1) shouldNotHaveScale 5
         10.444.toBigDecimal() shouldNotHaveScale 2
         0.toBigDecimal() shouldNotHaveScale 1
         BigDecimal.ZERO shouldNotHaveScale 2
      }

      test("shouldBeEqualIgnoringScale") {
         BigDecimal(10) shouldBeEqualIgnoringScale BigDecimal(10)
         BigDecimal(10) shouldBeEqualIgnoringScale BigDecimal(10.0)
         BigDecimal(10.00) shouldBeEqualIgnoringScale BigDecimal(10.0)

         BigDecimal(10) shouldNotBeEqualIgnoringScale BigDecimal(11)
         BigDecimal(10) shouldNotBeEqualIgnoringScale BigDecimal(11.0)

         shouldThrow<AssertionError> {
            BigDecimal(10) shouldNotBeEqualIgnoringScale BigDecimal(10)
         }

         shouldThrow<AssertionError> {
            BigDecimal(10) shouldNotBeEqualIgnoringScale BigDecimal(10.0)
         }
      }
   }
}
