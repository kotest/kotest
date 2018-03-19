package io.kotlintest.core.specs


class WordSpecExample : WordSpec() {
  init {
    "some test" should {
      "test something" {
        // test here
      }
    }
  }
}