package io.kotest.permutations

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class SharedConfigTest : FunSpec() {
   init {
      test("shared config should be used when passed to the permutation function") {

         val context = permutationConfiguration {
            iterations = 42 // run this property test 42 times
         }

         permutations(context) {
            forEach { }
         }.attempts shouldBe 42

         permutations(context) {
            forEach { }
         }.attempts shouldBe 42
      }
   }
}
