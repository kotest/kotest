package io.kotlintest.core.specs


class FlatSpecExample : FlatSpec() {
  init {
    "some text" should "do something" `in` {
      // test here
    }
  }
}