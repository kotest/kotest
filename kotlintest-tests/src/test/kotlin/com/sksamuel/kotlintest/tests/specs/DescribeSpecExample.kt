package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.specs.DescribeSpec

class DescribeSpecExample : DescribeSpec() {
  init {
    describe("some thing") {
      it("test name") {
        // test here
      }
      describe("some other thing") {
        context("with some context") {
          it("test name") {
            // test here
          }
        }
      }
    }
  }
}