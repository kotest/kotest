package com.sksamuel.kotlintest.specs

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.SpecIsolationMode
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class OneInstancePerTestOrderingTest : FreeSpec() {

  companion object {
    var string = ""
    var count = 0
  }

  override fun isInstancePerTest(): Boolean = true

  override fun specIsolationMode(): SpecIsolationMode? = SpecIsolationMode.InstancePerNode

  override fun afterSpecCompleted(description: Description, spec: Spec) {
    string shouldBe "a_ab_ae_abc_abd_"
  }

  init {
    "1" - {
      string += "a"
      "1.1" - {
        string += "b"
        "1.1.1" {
          string += "c"
        }
        "1.1.2" {
          string += "d"
        }
      }
      "1.2" {
        string += "e"
      }
      string += "_"
    }
  }
}