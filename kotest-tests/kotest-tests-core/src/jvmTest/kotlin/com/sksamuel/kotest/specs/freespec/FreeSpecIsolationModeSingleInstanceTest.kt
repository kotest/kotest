package com.sksamuel.kotest.specs.freespec

import io.kotest.Description
import io.kotest.IsolationMode
import io.kotest.Spec
import io.kotest.shouldBe
import io.kotest.specs.FreeSpec

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