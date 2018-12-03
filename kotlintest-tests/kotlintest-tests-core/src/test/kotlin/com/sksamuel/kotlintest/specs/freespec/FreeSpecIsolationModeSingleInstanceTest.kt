package com.sksamuel.kotlintest.specs.freespec

import io.kotlintest.TestIsolationMode
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class FreeSpecIsolationModeSingleInstanceTest : FreeSpec() {

  companion object {
    var string = ""
  }

  override fun isolationMode() = TestIsolationMode.SingleInstance

  override fun cleanupSpec(spec: Spec, results: Map<TestCase, TestResult>) {
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