package com.sksamuel.kotlintest.specs.wordspec

import io.kotlintest.specs.WordSpec

class WordSpecExample : WordSpec() {
  init {
    "a stack" should {
      "return the last element when popped" {
        // test here
      }
      "push elements to the top".config(invocations = 2) {
      }
    }

    "a queue" When {
      "iterated" Should {
        "return in insertion order"{
          // test here
        }
        "support removal".config(invocations = 2) {
        }
      }
    }
  }
}