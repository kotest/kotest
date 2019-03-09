package com.sksamuel.kotlintest.specs.freespec

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.IsolationMode
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class FreeSpecIsolationModeSingleInstanceTest : FreeSpec() {

  companion object {
    var string = ""
  }

  override fun isolationMode() = IsolationMode.SingleInstance

  override fun afterSpecCompleted(description: Description, spec: Spec) {
    string shouldBe "abcccde_"
  }

  init {
    "a" - {
      string += "a"
      "b" - {
        string += "b"
        // since we execute this test 3 times, and we are in single instance mode,
        // the letter c should appear 3 times in turn
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