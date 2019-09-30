package com.sksamuel.kotest.specs.freespec

import io.kotest.IsolationMode
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.shouldBe
import io.kotest.specs.FreeSpec

class FreeSpecInstancePerLeafTest : FreeSpec() {

  companion object {
    var string = ""
  }

  override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    string shouldBe "ab_acd_acef_aceg_hij_hik_"
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
        }
      }
      string += "_"
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
      }
      string += "_"
    }
  }
}