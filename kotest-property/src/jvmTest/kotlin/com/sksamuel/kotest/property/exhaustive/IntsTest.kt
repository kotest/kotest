package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.filter
import io.kotest.property.exhaustive.flatMap
import io.kotest.property.exhaustive.ints
import io.kotest.property.exhaustive.map

@EnabledIf(NotMacOnGithubCondition::class)
class IntsTest : FunSpec({
   test("should return all filtered Ints") {
      Exhaustive.ints(0..10)
         .filter { it % 2 == 0 }
         .map { it * 2 }
         .values shouldBe listOf(0, 4, 8, 12, 16, 20)
   }
   test("flatMap works too") {
      Exhaustive.ints(0..3)
         .flatMap { Exhaustive.ints(0..it) }
         .values shouldBe listOf(0, 0, 1, 0, 1, 2, 0, 1, 2, 3)
   }
})
