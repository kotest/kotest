package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.factor
import io.kotest.property.arbitrary.take

@EnabledIf(NotMacOnGithubCondition::class)
class FactorTest : FunSpec({
   test("factors of k") {
      Arb.factor(99).take(100).forEach { 99 % it shouldBe 0 }
   }
})
