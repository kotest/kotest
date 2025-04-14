package io.kotest.permutations

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec

@EnabledIf(LinuxOnlyGithubCondition::class)
class PermutationResultTest : FunSpec() {
   init {
      test("PermutationResult should contain eval count") {
      }
   }
}
