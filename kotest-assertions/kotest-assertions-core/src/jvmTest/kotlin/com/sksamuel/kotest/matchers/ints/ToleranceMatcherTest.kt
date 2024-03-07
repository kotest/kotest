package com.sksamuel.kotest.matchers.ints

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeWithinPercentageOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.checkAll

class ToleranceMatcherTest : FunSpec({

   test("Match equal numbers") {
      checkAll(Arb.int(-1000, 1000), Arb.numericDouble(Double.MIN_VALUE, 5.0)) { value, percentage ->
         value.shouldBeWithinPercentageOf(value, percentage)
      }
   }

   test("Refuse negative percentage") {
      shouldThrow<IllegalArgumentException> {
         1.shouldBeWithinPercentageOf(1, -0.1)
      }
   }

   test("Match close enough numbers") {
      checkAll(Arb.int(-1000, 1000), Arb.numericDouble(Double.MIN_VALUE, 5.0)) { value, percentage ->
         val delta = (percentage / 100).times(value).toInt()
         (value + delta).shouldBeWithinPercentageOf(value, percentage)
         (value - delta).shouldBeWithinPercentageOf(value, percentage)
      }
   }
})
