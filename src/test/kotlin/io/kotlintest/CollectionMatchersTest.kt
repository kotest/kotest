package io.kotlintest

import io.kotlintest.matchers.contain
import io.kotlintest.specs.WordSpec

class CollectionMatchersTest : WordSpec() {

  init {

    "CollectionMatchers.contain" should {
      "should test that a collection contains an element" with {
        val col = listOf(1, 2, 3)
        col should contain element 2
        expecting(TestFailedException::class) {
          col should contain element 4
        }
      }
    }
  }
}