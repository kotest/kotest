package com.sksamuel.kotest.engine.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.lang.Integer.sum

@EnabledIf(LinuxOnlyGithubCondition::class)
class UnicodeTestNameTest : BehaviorSpec({
   isolationMode = IsolationMode.InstancePerRoot

   Given("코테스트 코테스트 코테스트") {
      And("A가 1이고 B가 2일 때") {
         val a = 1
         val b = 2
         When("함수를 실행하면") {
            val result = sum(a, b)
            Then("결과값이 3") {
               result shouldBe 3
            }
         }
      }
      And("A가 2이고 B가 3일 때") {
         val a = 2
         val b = 3
         When("함수를 실행하면") {
            val result = sum(a, b)
            Then("결과값이 5") {
               result shouldBe 5
            }
         }
      }
   }
   Given("테스트 테스트 테스트") {
      And("B가 3일 때") {
         val b = 3
         When("함수를 실행하면") {
            val result = sum(0, b)
            Then("결과값이 3") {
               result shouldBe 3
            }
         }
      }
      And("B가 4일 때") {
         val b = 4
         When("함수를 실행하면") {
            val result = sum(0, b)
            Then("결과값이 4") {
               result shouldBe 4
            }
         }
      }
   }
})
