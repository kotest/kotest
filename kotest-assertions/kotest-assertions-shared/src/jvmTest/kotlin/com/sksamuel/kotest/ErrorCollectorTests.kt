package com.sksamuel.kotest

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class ErrorCollectorTests : FreeSpec({
   "Single assertion failed, returns the original failure" {
      shouldThrow<AssertionError> {
         assertSoftly {
            1 shouldBe 1
            1 shouldBe 2
         }
      }
   }
})
