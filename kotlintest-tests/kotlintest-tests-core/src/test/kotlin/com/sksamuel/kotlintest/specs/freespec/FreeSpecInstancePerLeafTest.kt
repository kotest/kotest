package com.sksamuel.kotlintest.specs.freespec

import io.kotlintest.TestIsolationMode
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class FreeSpecInstancePerLeafTest : FreeSpec() {

  companion object {
    var string = ""
  }

  override fun isolationMode(): TestIsolationMode = TestIsolationMode.InstancePerLeaf

  override fun cleanupSpec(spec: Spec, results: Map<TestCase, TestResult>) {
    string shouldBe "abz_hijwv_acdyz_hikwv_acefxyz_acegxyz_"
  }

  init {
    "1" - {
      string += "a"
      "1.1" {
        string += "b"
      }
      "1.2" - {
        string += "c"
        "1.2.1" {
          string += "d"
        }
        "1.2.2" - {
          string += "e"
          "1.2.2.1" {
            string += "f"
          }
          "1.2.2.2" {
            string += "g"
          }
          string += "x"
        }
        string += "y"
      }
      string += "z_"
    }
    "2" - {
      string += "h"
      "2.1" - {
        string += "i"
        "2.1.1" {
          string += "j"
        }
        "2.1.2" {
          string += "k"
        }
        string += "w"
      }
      string += "v_"
    }
  }
}