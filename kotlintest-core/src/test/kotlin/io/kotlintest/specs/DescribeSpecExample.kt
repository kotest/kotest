package io.kotlintest.specs


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