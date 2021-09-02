package com.sksamuel.kotest.matchers.ints

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeWithinPercentageOf
import io.kotest.matchers.should
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll
import io.kotest.property.forAll

class ToleranceMatcherTest : FunSpec({

   test("Match equal numbers") {
      Arb.bind(Arb.int(), Arb.double(0.0, 5.0)) { value, percentage ->
         value.shouldBeWithinPercentageOf(value, percentage)
      }
   }

   test("Refuse negative percentage") {
      shouldThrow<IllegalArgumentException> {
         1.shouldBeWithinPercentageOf(1, -0.1)
      }
   }

   test("Match close enough numbers") {
      Arb.bind(Arb.int(), Arb.double(0.0, 5.0)) { value, percentage ->
         value.shouldBeWithinPercentageOf((value - value.times(percentage / 100).toInt()), percentage)
         value.shouldBeWithinPercentageOf((value + value.times(percentage / 100).toInt()), percentage)
      }
   }
})
