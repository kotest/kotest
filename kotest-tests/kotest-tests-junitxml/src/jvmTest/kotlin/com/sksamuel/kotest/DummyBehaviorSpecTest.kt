package com.sksamuel.kotest

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.Order
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

// this is used to generate some data for the xml report
@Order(0)
@EnabledIf(LinuxOnlyGithubCondition::class)
class DummyBehaviorSpecTest : BehaviorSpec() {
   init {
      given("a given") {
         `when`("a when") {
            then("a then") {
               1 + 1 shouldBe 2
            }
            then("another then") {
               1 + 1 shouldBe 2
            }
         }
         `when`("another when") {
            then("a final then") {
               1 + 1 shouldBe 2
            }
         }
      }
   }
}
