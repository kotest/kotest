package com.sksamuel.kotlintest.specs

import io.kotlintest.specs.FunSpec


class FunSpecExample : FunSpec() {
  init {
    test("this is a test") {
      // test here
    }
    test("this test has config").config(invocations = 1, enabled = true) {
      // test here
    }
  }
}