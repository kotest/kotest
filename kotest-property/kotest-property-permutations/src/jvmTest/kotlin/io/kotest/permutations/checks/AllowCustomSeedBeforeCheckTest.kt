@file:OptIn(ExperimentalKotest::class)

package io.kotest.permutations.checks

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.permutations.PermutationConfiguration
import io.kotest.permutations.toContext

@EnabledIf(LinuxOnlyGithubCondition::class)
class AllowCustomSeedBeforeCheckTest : FunSpec({

   test("should throw when a custom seed is set and failOnSeed is true") {
      val context = PermutationConfiguration().apply {
         seed = 12345L
         failOnSeed = true
         forEach { }
      }.toContext()

      val ex = shouldThrow<IllegalStateException> {
         AllowCustomSeedBeforeCheck.check(context)
      }
      ex.message shouldBe "A seed is specified on this permutation but failOnSeed is true"
   }
})
