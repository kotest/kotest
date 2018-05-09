package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.ExpectSpec

class ExpectSpecExample : ExpectSpec() {
  init {
    context("some context") {
      expect("some test") {
        // test here
      }
      expect("some test 2").config(invocations = 2) {
        // test here
      }
    }
  }
}