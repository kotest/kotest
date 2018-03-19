package io.kotlintest.core.specs


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