package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.instant

@EnabledIf(LinuxOnlyGithubCondition::class)
class InstantArbTest : FunSpec() {
   init {
      test("Arb.instant() should generate N valid Instants(no exceptions)") {
         Arb.instant().generate(RandomSource.default()).take(8656).toList().size shouldBe 8656
      }
   }
}
