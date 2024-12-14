package io.kotest.extensions.allure

import io.kotest.core.spec.Order
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.qameta.allure.Epic

// this is used to generate some data for allure
@Order(0)
@Epic("my epic")
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
