package io.kotlintest.specs

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