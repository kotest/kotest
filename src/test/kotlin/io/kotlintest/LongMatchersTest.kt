package io.kotlintest

import io.kotlintest.matchers.be
import io.kotlintest.specs.FreeSpec

class LongMatchersTest : FreeSpec() {
  init {
    "Long matchers" - {
      "should test ge" with {
        1 should be gt 0
      }
    }
  }
}