package com.sksamuel.kotest.specs.wordspec

import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.specs.WordSpec

class WordSpecTest : WordSpec() {

  init {
    "a context" should {
      "have a test" {
        2.shouldBeGreaterThan(1)
      }
      "have another test" {
        2.shouldBeGreaterThan(1)
      }
      "have a test with config".config(invocations = 2) {

      }
    }

    "another context" When {

      "using when" Should {
        "have a test" {
          2.shouldBeGreaterThan(1)
        }
        "have a test with config".config(invocations = 2) {
          2.shouldBeGreaterThan(1)
        }
      }

    }
  }
}
