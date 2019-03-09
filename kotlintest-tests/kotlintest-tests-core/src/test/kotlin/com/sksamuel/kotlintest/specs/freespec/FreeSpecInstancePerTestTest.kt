package com.sksamuel.kotlintest.specs.freespec

import io.kotlintest.IsolationMode
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class FreeSpecInstancePerTestTest : FreeSpec() {

  companion object {
    var string = ""
  }

  override fun isolationMode() = IsolationMode.InstancePerTest

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    string shouldBe "a_ab_abccc_ad_ade_"
  }

  init {
    "a" - {
      string += "a"
      "b" - {
        string += "b"
        // since we execute this test 3 times, and we are in instance per test,
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