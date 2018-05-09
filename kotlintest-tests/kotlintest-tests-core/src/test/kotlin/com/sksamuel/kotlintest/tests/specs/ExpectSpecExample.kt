package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.AbstractExpectSpec


class ExpectSpecExample : AbstractExpectSpec() {
  init {
    context("some context") {
      expect("some test") {
        // test here
      }
      expect("some test").config(invocations = 2) {
        // test here
      }
    }
  }
}