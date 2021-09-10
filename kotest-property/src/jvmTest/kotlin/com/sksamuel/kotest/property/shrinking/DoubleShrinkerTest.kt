package com.sksamuel.kotest.property.shrinking

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveLowerBound
import io.kotest.matchers.collections.shouldHaveUpperBound
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.internal.doShrinking

class DoubleShrinkerTest : FunSpec() {
   init {
      test("shrunk Arb.numericDouble values should stay within bounds") {
         val min = 12363.0
         val max = 772183.0
         val generator = Arb.numericDouble(min = min, max = max)
         val (v, s) = generator.sample(RandomSource.seeded(39084345))
         val collector = mutableListOf(v)

         doShrinking(s, ShrinkingMode.Bounded(100)) {
            collector.add(it)
            it shouldBe 0.0
         }

         collector shouldHaveLowerBound min
         collector shouldHaveUpperBound max
      }
   }
}
