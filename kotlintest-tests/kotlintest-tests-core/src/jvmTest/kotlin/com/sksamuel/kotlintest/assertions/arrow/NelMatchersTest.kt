package com.sksamuel.kotlintest.assertions.arrow

import arrow.data.NonEmptyList
import io.kotlintest.assertions.arrow.nel.beSorted
import io.kotlintest.assertions.arrow.nel.contain
import io.kotlintest.assertions.arrow.nel.containAll
import io.kotlintest.assertions.arrow.nel.containNoNulls
import io.kotlintest.assertions.arrow.nel.containNull
import io.kotlintest.assertions.arrow.nel.containOnlyNulls
import io.kotlintest.assertions.arrow.nel.haveDuplicates
import io.kotlintest.assertions.arrow.nel.haveSize
import io.kotlintest.assertions.arrow.nel.shouldBeSingleElement
import io.kotlintest.assertions.arrow.nel.shouldBeSorted
import io.kotlintest.assertions.arrow.nel.shouldBeUnique
import io.kotlintest.assertions.arrow.nel.shouldContain
import io.kotlintest.assertions.arrow.nel.shouldContainAll
import io.kotlintest.assertions.arrow.nel.shouldContainNoNulls
import io.kotlintest.assertions.arrow.nel.shouldContainNull
import io.kotlintest.assertions.arrow.nel.shouldContainOnlyNulls
import io.kotlintest.assertions.arrow.nel.shouldHaveDuplicates
import io.kotlintest.assertions.arrow.nel.shouldHaveSize
import io.kotlintest.assertions.arrow.nel.shouldNotBeUnique
import io.kotlintest.assertions.arrow.nel.shouldNotContain
import io.kotlintest.assertions.arrow.nel.shouldNotContainOnlyNulls
import io.kotlintest.assertions.arrow.nel.shouldNotHaveDuplicates
import io.kotlintest.assertions.arrow.nel.shouldNotHaveSize
import io.kotlintest.assertions.arrow.nel.singleElement
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

        NonEmptyList.of(null).shouldContainNull()
      }
    }

    "sorted" should {
      "test that a collection is sorted" {
        NonEmptyList.of(1, 2, 3, 4) should beSorted<Int>()
        NonEmptyList.of(1, 2, 3, 4).shouldBeSorted()
        shouldThrow<AssertionError> {
          NonEmptyList.of(2, 1) should beSorted<Int>()
        }
      }
    }

    "haveDuplicates" should {
      "test that a collection is unique or not" {
        NonEmptyList.of(1, 2, 3, 3) should haveDuplicates()
        NonEmptyList.of(1, 2, 3, 4) shouldNot haveDuplicates()
        NonEmptyList.of(1, 2, 3, 3).shouldHaveDuplicates()
        NonEmptyList.of(1, 2, 3, 4).shouldNotHaveDuplicates()
      }
    }

    "beUnique" should {
      "test that a collection is unique or not" {
        NonEmptyList.of(1, 2, 3, 4).shouldBeUnique()
        NonEmptyList.of(1, 2, 3, 3).shouldNotBeUnique()
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

        NonEmptyList.of(1).shouldBeSingleElement(1)
      }
    }

    "should contain element" should {
      "test that a collection contains an element"  {
        NonEmptyList.of(1, 2, 3) should contain(2)
        NonEmptyList.of(1, 2, 3).shouldContain(2)
        NonEmptyList.of(1, 2, 3).shouldNotContain(4)
        shouldThrow<AssertionError> {
          NonEmptyList.of(1, 2, 3) should contain(4)
        }
      }
    }

    "haveSize" should {
      "test that a collection has a certain size" {
        NonEmptyList.of(1, 2, 3) should haveSize(3)
        NonEmptyList.of(1, 2, 3).shouldHaveSize(3)
        NonEmptyList.of(1, 2, 3).shouldNotHaveSize(2)
        shouldThrow<AssertionError> {
          NonEmptyList.of(1, 2, 3) should haveSize(2)
        }
      }
    }

    "containNoNulls" should {
      "test that a collection contains zero nulls"  {
        NonEmptyList.of(1, 2, 3) should containNoNulls()
        NonEmptyList.of(1, 2, 3).shouldContainNoNulls()
        NonEmptyList.of(null, null, null) shouldNot containNoNulls()
        NonEmptyList.of(1, null, null) shouldNot containNoNulls()
      }
    }

    "containOnlyNulls" should {
      "test that a collection contains only nulls"  {
        NonEmptyList.of(null, null, null) should containOnlyNulls()
        NonEmptyList.of(null, null, null).shouldContainOnlyNulls()
        NonEmptyList.of(1, null, null) shouldNot containOnlyNulls()
        NonEmptyList.of(1, 2, 3) shouldNot containOnlyNulls()
        NonEmptyList.of(1, 2, 3).shouldNotContainOnlyNulls()
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

        col.shouldContainAll(1, 2, 3, 4)
        col.shouldContainAll(1, 2, 3, 4, 5)
        col.shouldContainAll(3, 2, 1)
        col.shouldContainAll(5, 4, 3, 2, 1)

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