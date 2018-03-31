package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.ExpectSpec


class ExpectSpecExample : ExpectSpec() {
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