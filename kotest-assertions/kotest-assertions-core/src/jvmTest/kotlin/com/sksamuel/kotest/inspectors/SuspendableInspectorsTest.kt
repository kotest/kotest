package com.sksamuel.kotest.inspectors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAtLeastOne
import io.kotest.inspectors.forAtMostOne
import io.kotest.inspectors.forExactly
import io.kotest.inspectors.forNone
import io.kotest.inspectors.forOne
import io.kotest.inspectors.forSome
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

@EnabledIf(NotMacOnGithubCondition::class)
class SuspendableInspectorsTest : FunSpec() {
   init {
      test("all inspectors should support suspendable functions") {
         listOf(1, 2, 3).forOne {
            delay(1)
            it shouldBe 1
         }

         listOf(1, 2, 3).forExactly(1) {
            delay(1)
            it shouldBe 1
         }

         listOf(1, 2, 3).forAll {
            delay(1)
         }

         listOf(1, 2, 3).forNone {
            delay(1)
            it shouldBe 4
         }

         listOf(1, 2, 3).forSome {
            delay(1)
            it shouldBeLessThan 3
         }

         listOf(1, 2, 3).forAtLeastOne {
            delay(1)
            it shouldBeLessThan 3
         }

         listOf(1, 2, 3).forAtMostOne {
            delay(1)
            it shouldBe 1
         }
      }
   }
}
