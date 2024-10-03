package com.sksamuel.kotest.property.shrinking

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.collections.shouldHaveLowerBound
import io.kotest.matchers.collections.shouldHaveUpperBound
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.DoubleShrinker
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.internal.doShrinking

@EnabledIf(LinuxCondition::class)
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
            1.0 shouldBe 0.0 // failing assertion to keep on shrinking
         }

         collector shouldHaveLowerBound min
         collector shouldHaveUpperBound max
      }

      test("special values that cannot be shrunk") {
         val values = listOf(
            Double.NaN,
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
            Double.MIN_VALUE, -Double.MIN_VALUE,
            0.0, -0.0, 1.0, -1.0,
            10.0, -10.0, 100.0, 1e100
         ).map(::row).toTypedArray()

         forAll(*values) { value ->
            DoubleShrinker.shrink(value) should beEmpty()
         }
      }

      test("shrunken numeric values get smaller string representations") {
         val values = listOf(
            1.2 to 1.0, -1.2 to -1.0,
            12.0 to 10.0, -12.0 to -10.0,
            10000200003.0 to 10000200000.0,
            1234567890123456789E-40 to 1234567890123456E-37,
            4.9E-323 to 4.0E-323
         ).map { (input, output) -> row(input, output) }.toTypedArray()

         forAll(*values) { input, expectedOutput ->
            val actualOutput = DoubleShrinker.shrink(input)

            actualOutput shouldHaveAtMostSize 1
            actualOutput.firstOrNull() shouldBe expectedOutput
         }
      }
   }
}
