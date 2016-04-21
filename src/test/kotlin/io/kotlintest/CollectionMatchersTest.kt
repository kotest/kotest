package io.kotlintest

import io.kotlintest.matchers.contain
import io.kotlintest.specs.WordSpec
import java.util.*

class CollectionMatchersTest : WordSpec() {

  init {

    "CollectionMatchers.contain" should {
      "test that a collection contains an element"  {
        val col = listOf(1, 2, 3)

        col should contain element 2
        expecting(TestFailedException::class) {
          col should contain element 4
        }

      }
    }

    "CollectionMatchers.empty" should {
      "test that a collection contains an element"  {
        val col = listOf(1, 2, 3)

        expecting(TestFailedException::class) {
          col should beEmpty()
        }

        ArrayList<String>() should beEmpty()
      }
    }
  }
}