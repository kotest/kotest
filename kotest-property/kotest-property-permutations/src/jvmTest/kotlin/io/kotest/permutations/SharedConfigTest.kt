@file:Suppress("RETURN_VALUE_NOT_USED_COERCION")

package io.kotest.permutations

import io.kotest.common.ExperimentalKotest
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalKotest::class)
@EnabledIf(LinuxOnlyGithubCondition::class)
class SharedConfigTest : FunSpec() {
   init {
      test("shared config should be used when passed to the permutation function") {

         val context = permutationConfiguration {
            iterations = 42 // run this property test 42 times
         }

         permutations(context) {
            check { }
         }.attempts shouldBe 42

         permutations(context) {
            check { }
         }.attempts shouldBe 42
      }
   }
}
