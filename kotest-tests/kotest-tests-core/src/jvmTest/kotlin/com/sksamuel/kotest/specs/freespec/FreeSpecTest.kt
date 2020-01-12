package com.sksamuel.kotest.specs.freespec

import io.kotest.core.spec.SpecConfiguration
import io.kotest.shouldBe
import io.kotest.specs.FreeSpec

@Suppress("OverridingDeprecatedMember", "DEPRECATION")
class FreeSpecTest : FreeSpec() {

  private var count = 0

  override fun afterSpec(spec: SpecConfiguration) {
    count shouldBe 3
  }

  init {

    "context a" - {
      "b1" - {
        "c" {
          count += 1
        }
      }
      "b2" - {
        "d" {
          count += 2
        }
      }
    }


    "params" - {
      "support config".config(invocations = 5) {
      }
    }
  }
}
