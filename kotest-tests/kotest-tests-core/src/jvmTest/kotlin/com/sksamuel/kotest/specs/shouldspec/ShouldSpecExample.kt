package com.sksamuel.kotest.specs.shouldspec

import io.kotest.core.spec.style.ShouldSpec

class ShouldSpecExample : ShouldSpec() {
  init {
    should("top level test") {
      // test here
    }
    should("top level test with config").config(enabled = true) {
      // test here
    }
     context("a context") {
        should("do a test") {
           // test here
        }
        should("have config").config(enabled = true) {
           // test here
        }
     }
     context("another context") {
        context("a nested context") {
           should("do a test") {
          // test here
        }
      }
    }
  }
}
