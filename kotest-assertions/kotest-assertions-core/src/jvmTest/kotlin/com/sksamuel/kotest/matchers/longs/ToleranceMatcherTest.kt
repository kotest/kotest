package com.sksamuel.kotest.matchers.longs

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeWithinPercentageOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.checkAll

class ToleranceMatcherTest : FunSpec({

   test("Match equal numbers") {
      checkAll(Arb.long(), Arb.numericDouble(Double.MIN_VALUE, 5.0)) { value, percentage ->
         value.shouldBeWithinPercentageOf(value, percentage)
      }
   }

   test("Refuse negative percentage") {
      shouldThrow<IllegalArgumentException> {
         1L.shouldBeWithinPercentageOf(1, -0.1)
      }
   }

   test("Match close enough numbers") {
      checkAll(Arb.long(-1000L, 1000L), Arb.numericDouble(Double.MIN_VALUE, 5.0)) { value, percentage ->
         val delta = (percentage / 100).times(value).toLong()
         (value + delta).shouldBeWithinPercentageOf(value, percentage)
         (value - delta).shouldBeWithinPercentageOf(value, percentage)
      }
   }
})

