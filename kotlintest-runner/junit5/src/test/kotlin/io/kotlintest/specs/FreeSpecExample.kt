package io.kotlintest.specs


class FreeSpecExample : AbstractFreeSpec() {
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