package com.sksamuel.kotest.assertions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.AssertionMode
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class AssertionCounterFreeSpecTest : FreeSpec({
   assertions = AssertionMode.Error
   "container should not need to have an assertion" - {
      "neither should this container" - {
         "but this one does" {
            1 shouldBe 1
         }
      }
   }
})
