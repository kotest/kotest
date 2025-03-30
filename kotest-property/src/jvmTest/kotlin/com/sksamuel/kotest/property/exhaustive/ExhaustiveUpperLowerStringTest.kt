package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.upperLowerCase

@EnabledIf(NotMacOnGithubCondition::class)
class ExhaustiveUpperLowerStringTest : FreeSpec(
   {
      "Empty string" {
         Exhaustive.upperLowerCase("").values shouldBe listOf("")
      }

      "Exhaustive all cases" {
         Exhaustive.upperLowerCase("abc").values shouldContainExactlyInAnyOrder listOf(
            "abc",
            "abC",
            "aBc",
            "aBC",
            "Abc",
            "AbC",
            "ABc",
            "ABC"
         )
      }

      "with non-casing char" {
         Exhaustive.upperLowerCase("a.b").values shouldContainExactlyInAnyOrder listOf("a.b", "a.B", "A.b", "A.B")
      }
   }
)
