package io.kotest.samples.gradle

import io.kotest.specs.ExpectSpec

class ExpectSpecExampleTest : ExpectSpec() {
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