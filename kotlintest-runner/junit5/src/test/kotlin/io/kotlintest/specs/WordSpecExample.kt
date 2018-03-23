package io.kotlintest.specs

import io.kotlintest.runner.junit5.specs.WordSpec

class WordSpecExample : WordSpec() {
  init {
    "some test" should {
      "test something" {
        // test here
      }
    }
  }
}