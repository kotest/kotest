package com.sksamuel.kotest.engine.spec.style

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
