package io.kotlintest.samples.gradle

import io.kotlintest.matchers.haveLength
import io.kotlintest.specs.ExpectSpec
import io.kotlintest.specs.WordSpec

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