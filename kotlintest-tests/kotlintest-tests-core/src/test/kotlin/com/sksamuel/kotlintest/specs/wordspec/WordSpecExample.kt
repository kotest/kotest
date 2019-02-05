package com.sksamuel.kotlintest.specs.wordspec

import io.kotlintest.specs.WordSpec

class WordSpecExample : WordSpec() {
  init {
    "some test" should {
      "test something" {
        // test here
      }
      "allow config".config(invocations = 2) {
      }
    }

    "another test" When {
      "using when" Should {
        "test something"{
          // test here
        }
        "allow config".config(invocations = 2) {
        }
      }
    }
  }
}