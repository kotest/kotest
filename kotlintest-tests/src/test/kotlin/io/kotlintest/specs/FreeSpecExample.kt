package io.kotlintest.specs

import io.kotlintest.runner.junit5.specs.FreeSpec


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