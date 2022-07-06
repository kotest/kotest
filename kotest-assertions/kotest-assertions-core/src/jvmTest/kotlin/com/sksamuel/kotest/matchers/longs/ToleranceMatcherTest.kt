package com.sksamuel.kotest.matchers.longs

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeWithinPercentageOf
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.long

class ToleranceMatcherTest : FunSpec({

   test("Match equal numbers") {
      Arb.bind(Arb.long(), Arb.double(0.0, 5.0)) { value, percentage ->
         value.shouldBeWithinPercentageOf(value, percentage)
      }.sample(RandomSource.default())
   }

   test("Refuse negative percentage") {
      shouldThrow<IllegalArgumentException> {
         1L.shouldBeWithinPercentageOf(1, -0.1)
      }
   }

   test("Match close enough numbers") {
      Arb.bind(Arb.long(), Arb.double(0.0, 5.0)) { value, percentage ->
         val delta = (percentage / 100).times(value).toLong()
         (value + delta).shouldBeWithinPercentageOf(value, percentage)
         (value - delta).shouldBeWithinPercentageOf(value, percentage)
      }.sample(RandomSource.default())
   }
})

