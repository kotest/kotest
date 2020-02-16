package io.kotest.assertions.arrow

import arrow.core.NonEmptyList
import io.kotest.assertions.arrow.nel.beSorted
import io.kotest.assertions.arrow.nel.contain
import io.kotest.assertions.arrow.nel.containAll
import io.kotest.assertions.arrow.nel.containNoNulls
import io.kotest.assertions.arrow.nel.containNull
import io.kotest.assertions.arrow.nel.containOnlyNulls
import io.kotest.assertions.arrow.nel.haveDuplicates
import io.kotest.assertions.arrow.nel.haveElementAt
import io.kotest.assertions.arrow.nel.haveSize
import io.kotest.assertions.arrow.nel.shouldBeSingleElement
import io.kotest.assertions.arrow.nel.shouldBeSorted
import io.kotest.assertions.arrow.nel.shouldBeUnique
import io.kotest.assertions.arrow.nel.shouldContain
import io.kotest.assertions.arrow.nel.shouldContainAll
import io.kotest.assertions.arrow.nel.shouldContainElementAt
import io.kotest.assertions.arrow.nel.shouldContainNoNulls
import io.kotest.assertions.arrow.nel.shouldContainNull
import io.kotest.assertions.arrow.nel.shouldContainOnlyNulls
import io.kotest.assertions.arrow.nel.shouldHaveDuplicates
import io.kotest.assertions.arrow.nel.shouldHaveSize
import io.kotest.assertions.arrow.nel.shouldNotBeSingleElement
import io.kotest.assertions.arrow.nel.shouldNotBeSorted
import io.kotest.assertions.arrow.nel.shouldNotBeUnique
import io.kotest.assertions.arrow.nel.shouldNotContain
import io.kotest.assertions.arrow.nel.shouldNotContainAll
import io.kotest.assertions.arrow.nel.shouldNotContainElementAt
import io.kotest.assertions.arrow.nel.shouldNotContainNoNulls
import io.kotest.assertions.arrow.nel.shouldNotContainNull
import io.kotest.assertions.arrow.nel.shouldNotContainOnlyNulls
import io.kotest.assertions.arrow.nel.shouldNotHaveDuplicates
import io.kotest.assertions.arrow.nel.shouldNotHaveSize
import io.kotest.assertions.arrow.nel.singleElement
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot

class NelMatchersTest : WordSpec() {

  init {

    "containNull()" should {
      "test that a nel contains at least one null" {
        NonEmptyList.of(1, 2, null) should containNull()
        NonEmptyList.of(null) should containNull()
        NonEmptyList.of(1, 2) shouldNot containNull()

        NonEmptyList.of(null).shouldContainNull()
        NonEmptyList.of(1).shouldNotContainNull()
      }
    }

    "haveElementAt()" should {
      "test that a nel contains an element at the right position" {
        NonEmptyList.of(1, 2, null) should haveElementAt<Int?>(1, 2)
        NonEmptyList.of(1, 2, null).shouldContainElementAt(1, 2)
        NonEmptyList.of(1, 2, null).shouldNotContainElementAt(0, 42)
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

      "test that a collection is not sorted" {
        NonEmptyList.of(3, 2, 1, 4) shouldNot beSorted<Int>()
        NonEmptyList.of(5, 2, 3, 4).shouldNotBeSorted()
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

        NonEmptyList.of(1) shouldBeSingleElement 1
      }

      "test that a collection does not contain a single element"  {
        NonEmptyList.of(1, 2) shouldNotBeSingleElement 1
      }
    }

    "should contain element" should {
      "test that a collection contains an element"  {
        NonEmptyList.of(1, 2, 3) should contain(2)
        NonEmptyList.of(1, 2, 3) shouldContain 2
        NonEmptyList.of(1, 2, 3) shouldNotContain 4
        shouldThrow<AssertionError> {
          NonEmptyList.of(1, 2, 3) should contain(4)
        }
      }
    }

    "haveSize" should {
      "test that a collection has a certain size" {
        NonEmptyList.of(1, 2, 3) should haveSize(3)
        NonEmptyList.of(1, 2, 3) shouldHaveSize 3
        NonEmptyList.of(1, 2, 3) shouldNotHaveSize 2
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
        NonEmptyList.of(1, null, null).shouldNotContainNoNulls()
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
        col shouldContainAll listOf(1, 2, 3, 4)

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

      "test that a collection shouldNot containAll elements" {
        val col = NonEmptyList.of(1, 2, 3, 4, 5)

        col shouldNot containAll(99, 88, 77)

        col.shouldNotContainAll(99,88,77)
        col shouldNotContainAll listOf(99, 88, 77)
      }
    }
  }
}
