package com.sksamuel.kotest.specs.freespec

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@Suppress("OverridingDeprecatedMember", "DEPRECATION")
class FreeSpecTest : FreeSpec() {

  private var count = 0

  override fun afterSpec(spec: Spec) {
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
      "support config".config(enabled = true) {
      }
    }
  }
}
