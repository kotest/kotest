package com.sksamuel.kotlintest.specs.freespec

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestIsolationMode
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class FreeSpecIsolationModeInstancePerNodeTest : FreeSpec() {

  companion object {
    var string = ""
  }

  override fun testIsolationMode() = TestIsolationMode.InstancePerNode

  override fun afterSpecCompleted(description: Description, spec: Spec) {
    string shouldBe "a_ab_ad_abccc_ade_"
  }

  init {
    "a" - {
      string += "a"
      "b" - {
        string += "b"
        // since we execute this test 3 times, and we are in instance per nod,
        // the whole test should be re-executed 3 times
        "c".config(invocations = 3) {
          string += "c"
        }
      }
      "d" - {
        string += "d"
        "e" {
          string += "e"
        }
      }
      string += "_"
    }
  }
}