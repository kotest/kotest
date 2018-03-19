package io.kotlintest.specs


class WordSpecExample : WordSpec() {
  init {
    "some test" should {
      "test something" {
        // test here
      }
    }
  }
}