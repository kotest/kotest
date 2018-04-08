package com.sksamuel.kotlintest.tests.matchers.collections

import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.collections.contain
import io.kotlintest.matchers.collections.containNoNulls
import io.kotlintest.matchers.collections.containNull
import io.kotlintest.matchers.collections.containOnlyNulls
import io.kotlintest.matchers.collections.haveDuplicates
import io.kotlintest.matchers.collections.haveElementAt
import io.kotlintest.matchers.containAll
import io.kotlintest.matchers.containsInOrder
import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import io.kotlintest.shouldNot
import io.kotlintest.matchers.singleElement
import io.kotlintest.matchers.sorted
import io.kotlintest.specs.WordSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import java.util.*

class CollectionMatchersTest : WordSpec() {

  init {

    "haveElementAt" should {
      "test that a collection contains the specified element at the given index" {
        listOf("a", "b", "c") should haveElementAt(1, "b")
        listOf("a", "b", "c") shouldNot haveElementAt(1, "c")
        listOf("a", "b", null) should haveElementAt<String?>(2, null)
      }
    }

    "containNull()" should {
      "test that a collection contains at least one null" {
        listOf(1, 2, null) should containNull()
        listOf(null) should containNull()
        listOf(1, 2) shouldNot containNull()
      }
    }

    "sorted" should {
      "test that a collection is sorted" {
        listOf(1, 2, 3, 4) shouldBe sorted<Int>()
        shouldThrow<AssertionError> {
          listOf(2, 1) shouldBe sorted<Int>()
        }
      }
    }

    "haveDuplicates" should {
      "test that a collection is unique" {
        listOf(1, 2, 3, 3) should haveDuplicates()
        listOf(1, 2, 3, 4) shouldNot haveDuplicates()
      }
    }

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

    "should contain element" should {
      "test that a collection contains an element"  {
        val col = listOf(1, 2, 3)

        col should contain(2)

        shouldThrow<AssertionError> {
          col should contain(4)
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

    "contain" should {
      "test that a collection contains element x"  {
        val col = listOf(1, 2, 3)
        shouldThrow<AssertionError> {
          col should contain(4)
        }
        col should contain(2)
      }
    }

    "empty" should {
      "test that a collection contains an element"  {
        val col = listOf(1, 2, 3)

        shouldThrow<AssertionError> {
          col should beEmpty()
        }

        ArrayList<String>() should beEmpty()
      }
    }

    "containNoNulls" should {
      "test that a collection contains zero nulls"  {
        emptyList<String>() should containNoNulls()
        listOf(1, 2, 3) should containNoNulls()
        listOf(null, null, null) shouldNot containNoNulls()
        listOf(1, null, null) shouldNot containNoNulls()

      }
    }

    "containOnlyNulls" should {
      "test that a collection contains only nulls"  {
        emptyList<String>() should containOnlyNulls()
        listOf(null, null, null) should containOnlyNulls()
        listOf(1, null, null) shouldNot containOnlyNulls()
        listOf(1, 2, 3) shouldNot containOnlyNulls()
      }
    }

    "containInOrder" should {
      "test that a collection contains the same elements in the given order, duplicates permitted" {
        val col = listOf(1, 1, 2, 2, 3, 3)

        col should containsInOrder(1, 2, 3)
        col should containsInOrder(1)

        shouldThrow<AssertionError> {
          col should containsInOrder(1, 2, 6)
        }

        shouldThrow<AssertionError> {
          col should containsInOrder(4)
        }

        shouldThrow<AssertionError> {
          col should containsInOrder(2, 1, 3)
        }
      }
    }

    "containsAll" should {
      "test that a collection contains all the elements but in any order" {
        val col = listOf(1, 2, 3, 4, 5)

        col should containAll(1, 2, 3)
        col should containAll(3, 2, 1)
        col should containAll(5, 1)
        col should containAll(1, 5)
        col should containAll(1)
        col should containAll(5)

        shouldThrow<AssertionError> {
          col should containAll(1, 2, 6)
        }

        shouldThrow<AssertionError> {
          col should containAll(6)
        }

        shouldThrow<AssertionError> {
          col should containAll(0, 1, 2)
        }

        shouldThrow<AssertionError> {
          col should containAll(3, 2, 0)
        }
      }
    }
  }
}