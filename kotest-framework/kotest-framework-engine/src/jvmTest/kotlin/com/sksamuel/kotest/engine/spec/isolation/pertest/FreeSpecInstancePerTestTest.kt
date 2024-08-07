package com.sksamuel.kotest.engine.spec.isolation.pertest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FreeSpecInstancePerTestTest : FreeSpec() {

   companion object {
      var string = ""
   }

   override fun isolationMode() = IsolationMode.InstancePerTest

   init {

      beforeSpec {
         string += "-init-"
      }

      afterSpec {
         string shouldBe "-init-a-init-ab-init-abccc-init-d-init-de"
      }

      "a" - {
         string += "a"
         "b" - {
            string += "b"
            "c".config(invocations = 3) {
               string += "c"
            }
         }
      }
      "d" - {
         string += "d"
         "e" {
            string += "e"
         }
      }
   }
}
