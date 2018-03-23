package io.kotlintest.specs


class ShouldSpecExample : AbstractShouldSpec() {
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