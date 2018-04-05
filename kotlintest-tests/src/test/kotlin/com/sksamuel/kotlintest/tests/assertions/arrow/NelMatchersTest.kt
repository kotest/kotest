package com.sksamuel.kotlintest.tests.assertions.arrow

import arrow.data.NonEmptyList
import io.kotlintest.assertions.arrow.nel.contain
import io.kotlintest.assertions.arrow.nel.containAll
import io.kotlintest.assertions.arrow.nel.containNoNulls
import io.kotlintest.assertions.arrow.nel.containNull
import io.kotlintest.assertions.arrow.nel.containOnlyNulls
import io.kotlintest.assertions.arrow.nel.haveDuplicates
import io.kotlintest.assertions.arrow.nel.haveSize
import io.kotlintest.assertions.arrow.nel.singleElement
import io.kotlintest.assertions.arrow.nel.sorted
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

class NelMatchersTest : WordSpec() {

  init {

    "containNull()" should {
      "test that a nel contains at least one null" {
        NonEmptyList.of(1, 2, null) should containNull()
        NonEmptyList.of(null) should containNull()
        NonEmptyList.of(1, 2) shouldNot containNull()
      }
    }

    "sorted" should {
      "test that a collection is sorted" {
        NonEmptyList.of(1, 2, 3, 4) shouldBe sorted<Int>()
        shouldThrow<AssertionError> {
          NonEmptyList.of(2, 1) shouldBe sorted<Int>()
        }
      }
    }

    "haveDuplicates" should {
      "test that a collection is unique or not" {
        NonEmptyList.of(1, 2, 3, 3) should haveDuplicates()
        NonEmptyList.of(1, 2, 3, 4) shouldNot haveDuplicates()
      }
    }

    "singleElement" should {
      "test that a collection contains a single given element"  {
        NonEmptyList.of(1) shouldBe singleElement(1)
        shouldThrow<AssertionError> {
          NonEmptyList.of(1) shouldBe singleElement(2)
        }
        shouldThrow<AssertionError> {
          NonEmptyList.of(1, 2) shouldBe singleElement(2)
        }
      }
    }

    "should contain element" should {
      "test that a collection contains an element"  {
        NonEmptyList.of(1, 2, 3) should contain(2)
        shouldThrow<AssertionError> {
          NonEmptyList.of(1, 2, 3) should contain(4)
        }
      }
    }

    "haveSize" should {
      "test that a collection has a certain size" {
        NonEmptyList.of(1, 2, 3) should haveSize(3)
        shouldThrow<AssertionError> {
          NonEmptyList.of(1, 2, 3) should haveSize(2)
        }
      }
    }

    "contain" should {
      "test that a collection contains element x"  {
        val col = NonEmptyList.of(1, 2, 3)
        shouldThrow<AssertionError> {
          col should contain(4)
        }
        col should contain(2)
      }
    }

    "containNoNulls" should {
      "test that a collection contains zero nulls"  {
        NonEmptyList.of(1, 2, 3) should containNoNulls()
        NonEmptyList.of(null, null, null) shouldNot containNoNulls()
        NonEmptyList.of(1, null, null) shouldNot containNoNulls()
      }
    }

    "containOnlyNulls" should {
      "test that a collection contains only nulls"  {
        NonEmptyList.of(null, null, null) should containOnlyNulls()
        NonEmptyList.of(1, null, null) shouldNot containOnlyNulls()
        NonEmptyList.of(1, 2, 3) shouldNot containOnlyNulls()
      }
    }

    "containsAll" should {
      "test that a collection contains all the elements but in any order" {
        val col = NonEmptyList.of(1, 2, 3, 4, 5)

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