package io.kotlintest.specs

import io.kotlintest.runner.junit5.specs.DescribeSpec

class DescribeSpecExample : DescribeSpec() {
  init {
    describe("some context") {
      it("test name") {
        // test here
      }
      describe("nested contexts") {
        it("test name") {
          // test here
        }
      }
    }
  }
}