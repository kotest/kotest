package com.sksamuel.kotlintest.specs.freespec

import io.kotlintest.TestIsolationMode
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class FreeSpecIsolationModeInstancePerLeafTest : FreeSpec() {

  companion object {
    var string = ""
  }

  override fun isolationMode() = TestIsolationMode.InstancePerLeaf

  override fun cleanupSpec(spec: Spec, results: Map<TestCase, TestResult>) {
    string shouldBe "abccc_ade_"
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