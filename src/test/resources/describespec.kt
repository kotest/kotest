package com.sksamuel.kotest.specs.describe

import io.kotlintest.specs.DescribeSpec

class DescribeSpecExample : DescribeSpec() {
  init {

    describe("some thing") {
      it("test name") {
        // test here
      }
      context("with some context") {
        it("test name") {
          // test here
        }
        it("test name 2").config(invocations = 2) {
          // test here
        }
        context("yet another context") {
          it("test name") {
            // test here
          }
          it("test name 2").config(invocations = 2) {
            // test here
          }
        }
      }
    }
  }
}

