package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.ShouldSpec


class ShouldSpecExample : ShouldSpec() {
  init {
    should("top level test") {
      // test here
    }
    should("top level test with config").config(enabled = true) {
      // test here
    }
    "a context" {
      should("do a test") {
        // test here
      }
      should("have config").config(enabled = true) {
        // test here
      }
    }
    "another context" {
      "a nested context" {
        should("do a test") {
          // test here
        }
      }
    }
  }
}