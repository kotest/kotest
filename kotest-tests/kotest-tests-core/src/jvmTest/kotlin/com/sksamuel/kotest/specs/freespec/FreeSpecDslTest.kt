package com.sksamuel.kotest.specs.freespec

import io.kotest.core.spec.style.FreeSpec

class FreeSpecDslTest : FreeSpec() {

  init {

    "context" - {
      "another context" - {
        "the test" {
        }
      }
    }
  }
}
