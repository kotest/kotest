package com.sksamuel.kotest.matchers.floats

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.floats.shouldBeWithinPercentageOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float

class FloatPercentageTest : FunSpec() {
   init {
      context("Percentage") {
         test("Match equal numbers") {
            Arb.bind(Arb.float(), Arb.double(0.0, 5.0)) { value, percentage ->
               value.shouldBeWithinPercentageOf(value, percentage)
            }
         }

         test("Refuse negative percentage") {
            shouldThrow<IllegalArgumentException> {
               1f.shouldBeWithinPercentageOf(1f, -0.1)
            }
         }

         test("Match close enough numbers") {
            Arb.bind(Arb.float(), Arb.double(0.0, 5.0)) { value, percentage ->
               value.shouldBeWithinPercentageOf((value - value.times(percentage / 100).toFloat()), percentage)
               value.shouldBeWithinPercentageOf((value + value.times(percentage / 100)).toFloat(), percentage)
            }
         }
      }
   }
}
