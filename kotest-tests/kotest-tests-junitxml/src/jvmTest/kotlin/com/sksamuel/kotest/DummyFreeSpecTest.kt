package com.sksamuel.kotest

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.Order
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

// this is used to generate some data for the xml report
@Order(0)
@EnabledIf(LinuxOnlyGithubCondition::class)
class DummyFreeSpecTest : FreeSpec() {
   init {
      "1" - {
         "2" - {
            "3" {
               1 + 1 shouldBe 2
            }
            "4" - {
               "5" - {
                   "6" {
                      1 + 1 shouldBe 2
                   }
               }
            }
         }
      }
   }
}
