package com.sksamuel.kotest.matchers.doubles

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.percent
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.doubles.shouldBeWithinPercentageOf
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.checkAll

class DoubleToleranceTest : FunSpec({

   test("double with tolerance should include tolerance in error message") {
      shouldThrowAny {
         1.0 shouldBe (1.5 plusOrMinus 0.4)
      }.message shouldBe "1.0 should be equal to 1.5 within tolerance of 0.4 (lowest acceptable value is 1.1; highest acceptable value is 1.9)"
   }

   test("infinite double with finite tolerance should equal the same infinite double") {
      checkAll(Arb.numericDouble(min = 0.0)) { eps ->
         Double.NEGATIVE_INFINITY shouldBe (Double.NEGATIVE_INFINITY plusOrMinus eps)
         Double.POSITIVE_INFINITY shouldBe (Double.POSITIVE_INFINITY plusOrMinus eps)
      }
   }

   test("Allow for percentage tolerance") {
      1.5 shouldBe (1.0 plusOrMinus 50.percent)
      1.5 shouldNotBe (2.0 plusOrMinus 10.percent)

      -1.5 shouldBe (-1.0 plusOrMinus 50.percent)
      -1.5 shouldNotBe (-2.0 plusOrMinus 10.percent)
   }

   context("Percentage") {

      test("Match equal numbers") {
         checkAll(Arb.numericDouble(), Arb.numericDouble(Double.MIN_VALUE, 5.0)) { value, percentage ->
            value.shouldBeWithinPercentageOf(value, percentage)
         }
      }

      test("Refuse negative percentage") {
         shouldThrow<IllegalArgumentException> {
            1.0.shouldBeWithinPercentageOf(1.0, -0.1)
         }
      }

      test("Match close enough numbers") {
         checkAll(Arb.numericDouble(), Arb.numericDouble(Double.MIN_VALUE, 5.0)) { value, percentage ->
            val delta = value.times(percentage / 100)
            (value + delta).shouldBeWithinPercentageOf(value, percentage)
            (value - delta).shouldBeWithinPercentageOf(value, percentage)
         }
      }
   }
})

