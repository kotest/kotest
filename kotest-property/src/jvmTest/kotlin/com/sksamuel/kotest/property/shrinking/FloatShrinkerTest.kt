package com.sksamuel.kotest.property.shrinking

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.FloatShrinker

@EnabledIf(LinuxOnlyGithubCondition::class)
class FloatShrinkerTest : FunSpec() {
   init {
      test("special values that cannot be shrunk") {
         val values = listOf(
            Float.NaN,
            Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
            Float.MIN_VALUE, -Float.MIN_VALUE,
            0f, -0f, 1f, -1f,
            10f, -10f, 100f, 1e20f
         ).map(::row).toTypedArray()

         forAll(*values) { value ->
            FloatShrinker.shrink(value) should beEmpty()
         }
      }

      test("shrunken numeric values get smaller string representations") {
         val values = listOf(
            1.2f to 1f, -1.2f to -1f,
            12f to 10f, -12f to -10f,
            1002003f to 1002000f,
            1234567890123456789E-40f to 123456E-27f,
            1.4E-44f to 1.0E-44f
         ).map { (input, output) -> row(input, output) }.toTypedArray()

         forAll(*values) { input, expectedOutput ->
            val actualOutput = FloatShrinker.shrink(input)

            actualOutput shouldHaveAtMostSize 1
            actualOutput.firstOrNull() shouldBe expectedOutput
         }
      }
   }
}
