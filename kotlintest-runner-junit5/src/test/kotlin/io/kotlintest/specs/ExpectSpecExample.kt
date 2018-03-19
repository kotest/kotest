package io.kotlintest.specs


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