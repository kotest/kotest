package io.kotlintest.specs

import io.kotlintest.runner.junit5.specs.ShouldSpec


class ShouldSpecExample : ShouldSpec() {
  init {
    "some context" {
      "more context" {
        should("do something") {
          // test here
        }
      }
    }
  }
}