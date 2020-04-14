package com.sksamuel.kotest.matchers.doubles

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class DoubleTolerenceTest : FunSpec({

   test("double with tolerance should include tolerance in error message") {
      shouldThrowAny {
         1.0 shouldBe (1.5 plusOrMinus 0.4)
      }.message shouldBe "1.0 should be equal to 1.5 within tolerance of 0.4 (lowest acceptable value is 1.1; highest acceptable value is 1.9)"
   }
})

