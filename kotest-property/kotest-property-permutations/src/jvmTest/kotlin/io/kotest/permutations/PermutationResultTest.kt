package io.kotest.permutations

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec

@EnabledIf(NotMacOnGithubCondition::class)
class PermutationResultTest : FunSpec() {
   init {
      test("PermutationResult should contain eval count") {
      }
   }
}
