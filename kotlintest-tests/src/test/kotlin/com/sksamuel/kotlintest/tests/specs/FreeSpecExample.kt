package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.FreeSpec


class FreeSpecExample : FreeSpec() {
  init {
    "some context" - {
      "more context" - {
        "as many as you want" - {
          "then a test" {
            // test case
          }
        }
      }
    }
  }
}