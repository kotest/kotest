package com.sksamuel.kotest.specs.expect

import io.kotest.specs.ExpectSpec

class ExpectSpecExample : ExpectSpec() {
  init {
    context("some context") {
      expect("some test") {
        // test here
      }
      expect("some test 2").config(invocations = 2) {
        // test here
      }
      context("another nested context") {
        expect("some test") {
          // test here
        }
        expect("some test 2").config(invocations = 2) {
          // test here
        }
      }
    }
  }
}