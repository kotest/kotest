package com.sksamuel.kotest.matchers.doubles

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.numericDoubles
import io.kotest.property.checkAll

class DoubleToleranceTest : FunSpec({

   test("double with tolerance should include tolerance in error message") {
      shouldThrowAny {
         1.0 shouldBe (1.5 plusOrMinus 0.4)
      }.message shouldBe "1.0 should be equal to 1.5 within tolerance of 0.4 (lowest acceptable value is 1.1; highest acceptable value is 1.9)"
   }

   test("infinite double with finite tolerance should equal the same infinite double") {
      checkAll(Arb.numericDoubles(from = 0.0)) { eps ->
         Double.NEGATIVE_INFINITY shouldBe (Double.NEGATIVE_INFINITY plusOrMinus eps)
         Double.POSITIVE_INFINITY shouldBe (Double.POSITIVE_INFINITY plusOrMinus eps)
      }
   }
})

