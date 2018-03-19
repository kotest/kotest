package io.kotlintest.specs


class ShouldSpecExample : ShouldSpec() {
  init {
    "some context" {
      "more context" {
        should("do something") {
          // test here
        }
      }
    }
  }
}