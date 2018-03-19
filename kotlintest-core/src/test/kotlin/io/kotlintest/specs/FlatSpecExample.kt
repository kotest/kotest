package io.kotlintest.specs


class FlatSpecExample : FlatSpec() {
  init {
    "some text" should "do something" `in` {
      // test here
    }
  }
}