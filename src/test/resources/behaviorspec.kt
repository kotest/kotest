package com.sksamuel.kotest.specs.behavior

import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

class BehaviorSpecExample : BehaviorSpec() {

  init {
    given("a given") {
      `when`("a when") {
        then("a test") {
          "sam".shouldStartWith("s")
        }
        then("another test") {
          // test here
        }
      }
      `when`("another when") {
        then("a test") {
          // test here
        }
        then("a test with config").config(invocations = 3) {
          1 + 1 shouldBe 2
        }
      }
    }
  }
}