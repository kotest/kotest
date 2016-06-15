package io.kotlintest.matchers

import io.kotlintest.specs.FreeSpec

class LongMatchersTest : FreeSpec() {
  init {
    "Long matchers" - {
      "should test ge" {
        1 should be gt 0
      }
    }
  }
}