package com.sksamuel.kotlintest.specs.behavior

import io.kotlintest.specs.BehaviorSpec

class BehaviorSpecExample : BehaviorSpec() {

  init {
    given("a given") {
      `when`("a when") {
        then("a test") {
          // test here
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
          // test here
        }
      }
    }
  }
}