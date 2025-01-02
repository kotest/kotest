package com.sksamuel.kotest.engine.spec.isolation.perleaf

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FreeSpecInstancePerLeafTest : FreeSpec() {

   companion object {
      var string = ""
   }

   override fun isolationMode() = IsolationMode.InstancePerLeaf

   override suspend fun beforeSpec(spec: Spec) {
      string += "-init-"
   }

   init {

      afterProject {
         string shouldBe "-init-abccc-init-de"
      }

      "a" - {
         string += "a"
         "b" - {
            string += "b"
            // invocations are not restarted from the root level, so this will not cause a/b to execute again
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
