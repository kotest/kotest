package com.sksamuel.kotest

import io.kotest.assertions.AssertionFailedError
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@EnabledIf(NotMacOnGithubCondition::class)
class ErrorCollectorTests : FreeSpec(
   {
      "Single assertion failed, returns the original failure" {
         shouldThrow<AssertionFailedError> {
            assertSoftly {
               1 shouldBe 1
               1 shouldBe 2
            }
         }
      }
   }
)
