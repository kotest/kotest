package io.kotlintest.matchers

import io.kotlintest.specs.WordSpec
import java.util.*

class CollectionMatchersTest : WordSpec() {

  init {

    "singleElement" should {
      "test that a collection contains a single given element"  {
        listOf(1) shouldBe singleElement(1)
        shouldThrow<AssertionError> {
          listOf(1) shouldBe singleElement(2)
        }
        shouldThrow<AssertionError> {
          listOf(1, 2) shouldBe singleElement(2)
        }
      }
    }

    "CollectionMatchers.contain" should {
      "test that a collection contains an element"  {
        val col = listOf(1, 2, 3)

        col should contain element 2

        shouldThrow<AssertionError> {
          col should contain element 4
        }
      }
    }

    "haveSize" should {
      "test that a collection has a certain size" {
        val col1 = listOf(1, 2, 3)
        col1 should haveSize(3)
        shouldThrow<AssertionError> {
          col1 should haveSize(2)
        }

        val col2 = emptyList<String>()
        col2 should haveSize(0)
        shouldThrow<AssertionError> {
          col2 should haveSize(1)
        }
      }
    }

    "col should contain(x)" should {
      "test that a collection contains element x"  {
        val col = listOf(1, 2, 3)
        shouldThrow<AssertionError> {
          col should contain(4)
        }
        col should contain(2)
      }
    }

    "CollectionMatchers.empty" should {
      "test that a collection contains an element"  {
        val col = listOf(1, 2, 3)

        shouldThrow<AssertionError> {
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

        shouldThrow<AssertionError> {
          col should containInAnyOrder(1, 2, 6)
        }

        shouldThrow<AssertionError> {
          col should containInAnyOrder(6)
        }

        shouldThrow<AssertionError> {
          col should containInAnyOrder(0, 1, 2)
        }

        shouldThrow<AssertionError> {
          col should containInAnyOrder(3, 2, 0)
        }
      }
    }
  }
}