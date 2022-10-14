package com.sksamuel.kotest.matchers.floats

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.floats.shouldBeWithinPercentageOf
import io.kotest.matchers.floats.shouldNotBeWithinPercentageOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.arbitrary.numericFloat
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class FloatPercentageTest : FunSpec() {
   init {
      context("Percentage") {
         test("Match equal numbers") {
            checkAll(Arb.numericFloat(), Arb.numericDouble(Double.MIN_VALUE, 5.0)) { value, percentage ->
               value.shouldBeWithinPercentageOf(value, percentage)
            }
         }

         test("Refuse negative percentage") {
            shouldThrow<IllegalArgumentException> {
               1f.shouldBeWithinPercentageOf(1f, -0.1)
            }
         }

         test("Match close enough numbers") {
            checkAll(Arb.numericFloat(), Arb.numericDouble(Double.MIN_VALUE, 5.0)) { value, percentage ->
               val delta = (percentage / 100).times(value).toFloat()
               (value + delta).shouldBeWithinPercentageOf(value, percentage)
               (value - delta).shouldBeWithinPercentageOf(value, percentage)
            }
         }

         test("Correctly compare NaN") {
            (Float.NaN).shouldNotBeWithinPercentageOf(1f, 1.0)
         }

         test("Refuse comparing with NaN") {
            shouldThrow<IllegalArgumentException> {
               (0f).shouldBeWithinPercentageOf(Float.NaN, 1.0)
            }
         }

         test("Refuse comparing to zero with +inf precision") {
            shouldThrow<IllegalArgumentException> {
               0f.shouldBeWithinPercentageOf(0f, Double.POSITIVE_INFINITY)
            }
         }

         test("Refuse comparing to ranges around intinity") {
            shouldThrow<IllegalArgumentException> {
               0f.shouldBeWithinPercentageOf(Float.NEGATIVE_INFINITY, 1.0)
            }
            shouldThrow<IllegalArgumentException> {
               0f.shouldBeWithinPercentageOf(Float.POSITIVE_INFINITY, 1.0)
            }
         }
      }
   }
}
