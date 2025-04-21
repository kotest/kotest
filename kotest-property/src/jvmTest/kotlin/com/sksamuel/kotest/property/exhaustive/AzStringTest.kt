package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.azstring

@EnabledIf(LinuxOnlyGithubCondition::class)
class AzStringTest : ShouldSpec ({

   // Produces all letters a, b, c, ... , x, y, z
   val oneLetterPermutations = ('a'..'z').map { it.toString() }

   // Produces all permutations aa, ab, ac, ... , zx, zy, zz
   val twoLetterPermutations = ('a'..'z').flatMap { first ->
      ('a'..'z').map { second -> "$first$second" }
   }
   should("return the all letters for range 1..1") {
      Exhaustive.Companion.azstring(1..1).values shouldBe oneLetterPermutations
   }

   should("return the all two letter permutations for range 2..2") {
      Exhaustive.Companion.azstring(2..2).values shouldBe twoLetterPermutations
   }

   should("handle ranges") {
      Exhaustive.Companion.azstring(1..2).values shouldBe oneLetterPermutations + twoLetterPermutations
   }
})
