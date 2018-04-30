package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.AbstractExpectSpec


class ExpectSpecExample : AbstractExpectSpec() {
  init {
    context("some context") {
      expect("some test") {
        // test here
      }
      context("nested context even") {
        expect("some test") {
          // test here
        }
      }
    }
  }
}