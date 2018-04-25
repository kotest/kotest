package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.ShouldSpec


class ShouldSpecExample : ShouldSpec() {
  init {
    "a context" {
      should("do a test") {
        // test here
      }
    }
    "some context" {
      "more context" {
        should("allow config on test").config(invocations = 1) {
          // test here
        }
      }
    }
  }
}