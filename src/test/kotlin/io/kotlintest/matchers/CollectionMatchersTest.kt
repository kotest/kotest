package io.kotlintest.matchers

import io.kotlintest.TestFailedException
import io.kotlintest.specs.WordSpec
import java.util.*

class CollectionMatchersTest : WordSpec() {

  init {

    "CollectionMatchers.contain" should {
      "test that a collection contains an element"  {
        val col = listOf(1, 2, 3)

        col should contain element 2

        shouldThrow<TestFailedException> {
          col should contain element 4
        }
      }
    }

    "CollectionMatchers.empty" should {
      "test that a collection contains an element"  {
        val col = listOf(1, 2, 3)

        shouldThrow<TestFailedException> {
          col should beEmpty()
        }

        ArrayList<String>() should beEmpty()
      }
    }

    "containInAnyOrder" should {
      "test that a collection contains all the elements but in any order" {
        val col = listOf(1, 2, 3, 4, 5)

        col should containInAnyOrder(1, 2, 3)
        col should containInAnyOrder(3, 2, 1)
        col should containInAnyOrder(5, 1)
        col should containInAnyOrder(1, 5)
        col should containInAnyOrder(1)
        col should containInAnyOrder(5)

        shouldThrow<TestFailedException> {
          col should containInAnyOrder(1, 2, 6)
        }

        shouldThrow<TestFailedException> {
          col should containInAnyOrder(6)
        }

        shouldThrow<TestFailedException> {
          col should containInAnyOrder(0, 1, 2)
        }

        shouldThrow<TestFailedException> {
          col should containInAnyOrder(3, 2, 0)
        }
      }
    }
  }
}