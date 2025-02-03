package io.kotest.assertions.arrow.core

import arrow.core.NonEmptyList
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class NelMatchers : StringSpec({
  "containNull: a nel contains at least one null" {
    NonEmptyList(1, listOf(2, null)).shouldContainNull()
    NonEmptyList(null, listOf()).shouldContainNull()
    NonEmptyList(1, listOf(2)).shouldNotContainNull()
    NonEmptyList(1, listOf()).shouldNotContainNull()
  }

  "haveElementAt: a nel contains an element at the right position" {
    NonEmptyList(1, listOf(2, null)).shouldHaveElementAt(1, 2)
    NonEmptyList(1, listOf(2, null)).shouldNotHaveElementAt(0, 42)
  }

  "a collection is sorted" {
    NonEmptyList(1, listOf(2, 3, 4)).shouldBeSorted()
    shouldThrow<AssertionError> {
      NonEmptyList(2, listOf(1)).shouldBeSorted()
    }
  }

  "a collection is not sorted" {
    NonEmptyList(3, listOf(2, 1, 4)).shouldNotBeSorted()
    NonEmptyList(5, listOf(2, 3, 4)).shouldNotBeSorted()
  }

  "haveDuplicates: a collection is unique or not" {
    NonEmptyList(1, listOf(2, 3, 3)).shouldContainDuplicates()
    NonEmptyList(1, listOf(2, 3, 4)).shouldNotContainDuplicates()
  }

  "beUnique: a collection is unique or not" {
    NonEmptyList(1, listOf(2, 3, 4)).shouldBeUnique()
    NonEmptyList(1, listOf(2, 3, 3)).shouldNotBeUnique()
  }

  "a collection contains a single given element"  {
    NonEmptyList(1, listOf()) shouldHaveSingleElement 1
    shouldThrow<AssertionError> {
      NonEmptyList(1, listOf()) shouldHaveSingleElement 2
    }
    shouldThrow<AssertionError> {
      NonEmptyList(1, listOf(2)) shouldHaveSingleElement 2
    }

    NonEmptyList(1, listOf()) shouldHaveSingleElement 1
  }

  "a collection does not contain a single element"  {
    NonEmptyList(1, listOf(2)) shouldNotHaveSingleElement 1
  }

  "a collection contains an element"  {
    NonEmptyList(1, listOf(2, 3)) shouldContain 2
    NonEmptyList(1, listOf(2, 3)) shouldNotContain 4
    shouldThrow<AssertionError> {
      NonEmptyList(1, listOf(2, 3)) shouldContain 4
    }
  }

  "a collection has a certain size" {
    NonEmptyList(1, listOf(2, 3)) shouldHaveSize 3
    NonEmptyList(1, listOf(2, 3)) shouldNotHaveSize 2
    shouldThrow<AssertionError> {
      NonEmptyList(1, listOf(2, 3)) shouldHaveSize 2
    }
  }

  "a collection contains zero nulls"  {
    NonEmptyList(1, listOf(2, 3)).shouldContainNoNulls()
    NonEmptyList(null, listOf(null, null)).shouldNotContainNoNulls()
    NonEmptyList(1, listOf(null, null)).shouldNotContainNoNulls()
  }

  "a collection contains only nulls"  {
    NonEmptyList(null, listOf(null, null)).shouldContainOnlyNulls()
    NonEmptyList(1, listOf(null, null)).shouldNotContainOnlyNulls()
    NonEmptyList(1, listOf(2, 3)).shouldNotContainOnlyNulls()
  }

  "a collection contains all the elements but in any order" {
    val col = NonEmptyList(1, listOf(2, 3, 4, 5))

    col.shouldContainAll(1, 2, 3)
    col.shouldContainAll(3, 2, 1)
    col.shouldContainAll(5, 1)
    col.shouldContainAll(1, 5)
    col.shouldContainAll(1)
    col.shouldContainAll(5)

    col.shouldContainAll(1, 2, 3, 4)
    col.shouldContainAll(1, 2, 3, 4, 5)
    col.shouldContainAll(3, 2, 1)
    col.shouldContainAll(5, 4, 3, 2, 1)
    col shouldContainAll listOf(1, 2, 3, 4)

    shouldThrow<AssertionError> {
      col.shouldContainAll(1, 2, 6)
    }

    shouldThrow<AssertionError> {
      col.shouldContainAll(6)
    }

    shouldThrow<AssertionError> {
      col.shouldContainAll(0, 1, 2)
    }

    shouldThrow<AssertionError> {
      col.shouldContainAll(3, 2, 0)
    }
  }

  "a collection shouldNot containAll elements" {
    val col = NonEmptyList(1, listOf(2, 3, 4, 5))

    col.shouldNotContainAll(99, 88, 77)
    col shouldNotContainAll listOf(99, 88, 77)
  }
})
