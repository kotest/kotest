package com.sksamuel.kotest.specs.expect

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.datatest.withData

class ExpectSpecExample : ExpectSpec() {
  init {
    context("some context") {
      expect("some test") {
        // test here
      }
      expect("some test with config").config(invocations = 2) {
        // test here
      }
      context("another nested context") {
        expect("some test") {
          // test here
        }
        expect("some test with config").config(invocations = 2) {
          // test here
        }
      }
    }

    withData(1, 2, 3, 4, 5) { value ->
      // test here
    }
  }
}
