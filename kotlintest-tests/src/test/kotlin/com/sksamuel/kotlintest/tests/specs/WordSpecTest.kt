package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.specs.WordSpec

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
  }
}