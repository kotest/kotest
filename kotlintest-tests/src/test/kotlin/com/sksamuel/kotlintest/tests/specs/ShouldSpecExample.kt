package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.ShouldSpec


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