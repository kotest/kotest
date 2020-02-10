package com.sksamuel.kotest.specs.isolation.leaf

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

private var buffer = ""

class BehaviorSpecInstancePerLeafTest : BehaviorSpec() {

   override fun isolationMode() = IsolationMode.InstancePerLeaf

   init {

      afterProject {
         buffer shouldBe "abacdacefghifghjfgklfgkm"
      }

      Given("a") {
         buffer += "a"
         When("b") {
            buffer += "b"
         }
         When("c") {
            buffer += "c"
            Then("d") {
               buffer += "d"
            }
            Then("e") {
               buffer += "e"
            }
         }
      }
      Given("f") {
         buffer += "f"
         And("g") {
            buffer += "g"
            When("h") {
               buffer += "h"
               Then("i") {
                  buffer += "i"
               }
               Then("j") {
                  buffer += "j"
               }
            }
            And("k") {
               buffer += "k"
               Then("l") {
                  buffer += "l"
               }
               Then("m") {
                  buffer += "m"
               }
            }
         }
      }
   }
}
