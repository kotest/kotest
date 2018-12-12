package com.sksamuel.kt.matchers.collections

import io.kotlintest.matchers.collections.atLeastSize
import io.kotlintest.matchers.collections.atMostSize
import io.kotlintest.matchers.collections.beEmpty
import io.kotlintest.matchers.collections.beLargerThan
import io.kotlintest.matchers.collections.beSameSizeAs
import io.kotlintest.matchers.collections.beSmallerThan
import io.kotlintest.matchers.collections.contain
import io.kotlintest.matchers.collections.containAll
import io.kotlintest.matchers.collections.containDuplicates
import io.kotlintest.matchers.collections.containExactly
import io.kotlintest.matchers.collections.containExactlyInAnyOrder
import io.kotlintest.matchers.collections.containNoNulls
import io.kotlintest.matchers.collections.containNull
import io.kotlintest.matchers.collections.containOnlyNulls
import io.kotlintest.matchers.collections.containsInOrder
import io.kotlintest.matchers.collections.endWith
import io.kotlintest.matchers.collections.haveElementAt
import io.kotlintest.matchers.collections.haveSize
import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.collections.shouldBeLargerThan
import io.kotlintest.matchers.collections.shouldBeSameSizeAs
import io.kotlintest.matchers.collections.shouldBeSmallerThan
import io.kotlintest.matchers.collections.shouldBeSorted
import io.kotlintest.matchers.collections.shouldBeSortedWith
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.collections.shouldContainDuplicates
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.matchers.collections.shouldContainNoNulls
import io.kotlintest.matchers.collections.shouldContainNull
import io.kotlintest.matchers.collections.shouldContainOnlyNulls
import io.kotlintest.matchers.collections.shouldEndWith
import io.kotlintest.matchers.collections.shouldExist
import io.kotlintest.matchers.collections.shouldHaveAtLeastSize
import io.kotlintest.matchers.collections.shouldHaveAtMostSize
import io.kotlintest.matchers.collections.shouldHaveElementAt
import io.kotlintest.matchers.collections.shouldHaveSingleElement
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.collections.shouldNotBeEmpty
import io.kotlintest.matchers.collections.shouldNotBeSorted
import io.kotlintest.matchers.collections.shouldNotBeSortedWith
import io.kotlintest.matchers.collections.shouldNotContainAll
import io.kotlintest.matchers.collections.shouldNotContainDuplicates
import io.kotlintest.matchers.collections.shouldNotContainExactly
import io.kotlintest.matchers.collections.shouldNotContainExactlyInAnyOrder
import io.kotlintest.matchers.collections.shouldNotContainNoNulls
import io.kotlintest.matchers.collections.shouldNotContainNull
import io.kotlintest.matchers.collections.shouldNotContainOnlyNulls
import io.kotlintest.matchers.collections.shouldNotEndWith
import io.kotlintest.matchers.collections.shouldNotHaveElementAt
import io.kotlintest.matchers.collections.shouldNotHaveSize
import io.kotlintest.matchers.collections.shouldNotStartWith
import io.kotlintest.matchers.collections.shouldStartWith
import io.kotlintest.matchers.collections.singleElement
import io.kotlintest.matchers.collections.sorted
import io.kotlintest.matchers.collections.startWith
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldFail
import io.kotlintest.shouldHave
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotHave
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import java.util.ArrayList
import java.util.Comparator

class CollectionMatchersTest : WordSpec() {

  val countdown = (10 downTo 0).toList()
  val asc = { a: Int, b: Int -> a - b }
  val desc = { a: Int, b: Int -> b - a }

  init {

    "a descending non-empty list" should {
      "fail to ascend" {
        shouldFail {
          countdown.shouldBeSortedWith(asc)
        }
      }

      "descend" {
        countdown.shouldBeSortedWith(desc)
      }

      "not ascend" {
        countdown.shouldNotBeSortedWith(asc)
      }

      "fail not to descend" {
        shouldFail {
          countdown.shouldNotBeSortedWith(desc)
        }
      }
    }

    "sortedWith" should {
      val items = listOf(
          1 to "I",
          2 to "II",
          4 to "IV",
          5 to "V",
          6 to "VI",
          9 to "IX",
          10 to "X"
      )

      "work on non-Comparable given a Comparator" {
        items.shouldBeSortedWith(Comparator { a, b -> asc(a.first, b.first) })
      }

      "work on non-Comparable given a compare function" {
        items.shouldBeSortedWith { a, b -> asc(a.first, b.first) }
      }
    }

    "haveElementAt" should {
      "test that a collection contains the specified element at the given index" {
        listOf("a", "b", "c") should haveElementAt(1, "b")
        listOf("a", "b", "c") shouldNot haveElementAt(1, "c")
        listOf("a", "b", null) should haveElementAt(2, null)

        listOf("a", "b", "c").shouldHaveElementAt(1, "b")
        listOf("a", "b", "c").shouldNotHaveElementAt(1, "c")
        listOf("a", "b", null).shouldHaveElementAt(2, null)
      }
      "support type inference for subtypes of collection" {
        val tests = listOf(
                TestSealed.Test1("test1"),
                TestSealed.Test2(2)
        )
        tests should haveElementAt(0, TestSealed.Test1("test1"))
        tests.shouldHaveElementAt(1, TestSealed.Test2(2))
      }
    }

    "containNull()" should {
      "test that a collection contains at least one null" {
        listOf(1, 2, null) should containNull()
        listOf(null) should containNull()
        listOf(1, 2) shouldNot containNull()

        listOf(1, 2, null).shouldContainNull()
        listOf(null).shouldContainNull()
        listOf(1, 2).shouldNotContainNull()
      }
    }

    "sorted" should {
      "test that a collection is sorted" {
        listOf(1, 2, 3, 4) shouldBe sorted<Int>()
        shouldThrow<AssertionError> {
          listOf(2, 1) shouldBe sorted<Int>()
        }
        listOf(1, 2, 6, 9).shouldBeSorted()
        shouldThrow<AssertionError> {
          listOf(2, 1).shouldBeSorted()
        }.message.shouldBe("List [2,1] should be sorted. Element 2 at index 0 was greater than element 1")
        shouldThrow<AssertionError> {
          listOf(1, 2, 3).shouldNotBeSorted()
        }.message.shouldBe("List [1,2,3] should not be sorted")
      }
    }

    "haveDuplicates" should {
      "test that a collection is unique" {
        listOf(1, 2, 3, 3) should containDuplicates()
        listOf(1, 2, 3, 4) shouldNot containDuplicates()
        listOf(1, 2, 3, 3).shouldContainDuplicates()
        listOf(1, 2, 3, 4).shouldNotContainDuplicates()
      }
    }

    "singleElement" should {
      "test that a collection contains a single given element"  {
        listOf(1) shouldBe singleElement(1)
        listOf(1).shouldHaveSingleElement(1)
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

    "shouldBeLargerThan" should {
      "test that a collection is larger than another collection"  {
        val col1 = listOf(1, 2, 3)
        val col2 = setOf(1, 2, 3, 4)

        col2.shouldBeLargerThan(col1)
        col2 should beLargerThan(col1)
        col1 shouldNot beLargerThan(col2)

        shouldThrow<AssertionError> {
          col1.shouldBeLargerThan(col2)
        }.message shouldBe "Collection of size 3 should be larger than collection of size 4"
      }
    }

    "shouldBeSmallerThan" should {
      "test that a collection is smaller than another collection"  {
        val col1 = listOf(1, 2, 3)
        val col2 = setOf(1, 2, 3, 4)

        col1.shouldBeSmallerThan(col2)
        col1 should beSmallerThan(col2)
        col2 shouldNot beSmallerThan(col1)

        shouldThrow<AssertionError> {
          col2.shouldBeSmallerThan(col1)
        }.message shouldBe "Collection of size 4 should be smaller than collection of size 3"
      }
    }

    "shouldBeSameSizeAs" should {
      "test that a collection is the same size as another collection"  {
        val col1 = listOf(1, 2, 3)
        val col2 = setOf(1, 2, 3)
        val col3 = listOf(1, 2, 3, 4)

        col1.shouldBeSameSizeAs(col2)
        col1 should beSameSizeAs(col2)
        col1 shouldNot beSameSizeAs(col3)

        shouldThrow<AssertionError> {
          col1.shouldBeSameSizeAs(col3)
        }.message shouldBe "Collection of size 3 should be the same size as collection of size 4"
      }
    }

    "haveSize" should {
      "test that a collection has a certain size" {
        val col1 = listOf(1, 2, 3)
        col1 should haveSize(3)
        col1.shouldHaveSize(3)
        shouldThrow<AssertionError> {
          col1 should haveSize(2)
        }

        val col2 = emptyList<String>()
        col2 should haveSize(0)
        shouldThrow<AssertionError> {
          col2 should haveSize(1)
        }

        listOf(1, 2, 3).shouldNotHaveSize(1)
        listOf(1, 2, 3).shouldNotHaveSize(4)

        shouldThrow<AssertionError> {
          listOf(1, 2, 3).shouldNotHaveSize(3)
        }.message.shouldBe("Collection should not have size 3")

      }
    }

    "shouldExist" should {
      "test that a collection contains at least one element that matches a predicate" {
        val list = listOf(1, 2, 3)
        list.shouldExist { it == 2 }
      }
    }

    "shouldHaveAtLeastSize" should {
      "test that a collection has at least a certain number of elements" {
        val list = listOf(1, 2, 3)

        list.shouldHaveAtLeastSize(2)
        list shouldHave atLeastSize(2)

        val set = setOf(1, 2, 3)
        set.shouldHaveAtLeastSize(3)
        set shouldHave atLeastSize(3)

        shouldThrow<AssertionError> {
          list.shouldHaveAtLeastSize(4)
        }.message shouldBe "Collection should contain at least 4 elements"

        shouldThrow<AssertionError> {
          list shouldHave atLeastSize(4)
        }.message shouldBe "Collection should contain at least 4 elements"

        shouldThrow<AssertionError> {
          list shouldNotHave atLeastSize(2)
        }.message.shouldBe("Collection should contain less than 2 elements")
      }
    }

    "shouldHaveAtMostSize" should {
      "test that a collection has at least a certain number of elements" {
        val list = listOf(1, 2, 3)

        list.shouldHaveAtMostSize(3)
        list shouldHave atMostSize(3)

        list.shouldHaveAtMostSize(4)
        list shouldHave atMostSize(4)

        val set = setOf(1, 2, 3)
        set.shouldHaveAtMostSize(3)
        set shouldHave atMostSize(3)

        set.shouldHaveAtMostSize(4)
        set shouldHave atMostSize(4)

        shouldThrow<AssertionError> {
          list.shouldHaveAtMostSize(2)
        }.message shouldBe "Collection should contain at most 2 elements"

        shouldThrow<AssertionError> {
          list shouldHave atMostSize(2)
        }.message shouldBe "Collection should contain at most 2 elements"

        shouldThrow<AssertionError> {
          list shouldNotHave atMostSize(4)
        }.message.shouldBe("Collection should contain more than 4 elements")
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

      "support type inference for subtypes of collection" {
        val tests = listOf(
                TestSealed.Test1("test1"),
                TestSealed.Test2(2)
        )
        tests should contain(TestSealed.Test1("test1"))
        tests.shouldContain(TestSealed.Test2(2))
      }

      "print errors unambiguously"  {
        shouldThrow<AssertionError> {
          listOf<Any>(1, 2).shouldContain(listOf<Any>(1L, 2L))
        }.message shouldBe "Collection should contain element [1L, 2L]"
      }
    }

    "containExactly" should {
      "test that a collection contains given elements exactly"  {
        val actual = listOf(1, 2, 3)
        emptyList<Int>() should containExactly()
        actual should containExactly(1, 2, 3)
        actual.shouldContainExactly(1, 2, 3)
        actual.shouldContainExactly(linkedSetOf(1, 2, 3))

        actual shouldNot containExactly(1, 2)
        actual.shouldNotContainExactly(3, 2, 1)
        actual.shouldNotContainExactly(listOf(5, 6, 7))
        shouldThrow<AssertionError> {
          actual should containExactly(1, 2)
        }
        shouldThrow<AssertionError> {
          actual should containExactly(1, 2, 3, 4)
        }
        shouldThrow<AssertionError> {
          actual.shouldContainExactly(3, 2, 1)
        }
      }

      "print errors unambiguously"  {
        shouldThrow<AssertionError> {
          listOf<Any>(1L, 2L).shouldContainExactly(listOf<Any>(1, 2))
        }.message shouldBe "Collection should be exactly [1, 2] but was [1L, 2L]"
      }
    }

    "containExactlyInAnyOrder" should {
      "test that a collection contains given elements in any order"  {
        val actual = listOf(1, 2, 3)
        actual should containExactlyInAnyOrder(1, 2, 3)
        actual.shouldContainExactlyInAnyOrder(3, 2, 1)
        actual.shouldContainExactlyInAnyOrder(linkedSetOf(2, 1, 3))

        actual shouldNot containExactlyInAnyOrder(1, 2)
        actual.shouldNotContainExactlyInAnyOrder(1, 2, 3, 4)
        actual.shouldNotContainExactlyInAnyOrder(listOf(5, 6, 7))
        shouldThrow<AssertionError> {
          actual should containExactlyInAnyOrder(1, 2)
        }
        shouldThrow<AssertionError> {
          actual should containExactlyInAnyOrder(1, 2, 3, 4)
        }
      }

      "print errors unambiguously"  {
        shouldThrow<AssertionError> {
          listOf<Any>(1L, 2L).shouldContainExactlyInAnyOrder(listOf<Any>(1, 2))
        }.message shouldBe "Collection should contain [1, 2] in any order, but was [1L, 2L]"
      }
    }

    "empty" should {
      "test that a collection contains an element"  {
        val col = listOf(1, 2, 3)

        shouldThrow<AssertionError> {
          col should beEmpty()
        }.message.shouldBe("Collection should be empty but contained [1, 2, 3]")

        shouldThrow<AssertionError> {
          col.shouldBeEmpty()
        }.message.shouldBe("Collection should be empty but contained [1, 2, 3]")

        listOf(1, 2, 3).shouldNotBeEmpty()

        ArrayList<String>() should beEmpty()
      }
    }

    "containNoNulls" should {
      "test that a collection contains zero nulls"  {
        emptyList<String>() should containNoNulls()
        listOf(1, 2, 3) should containNoNulls()
        listOf(null, null, null) shouldNot containNoNulls()
        listOf(1, null, null) shouldNot containNoNulls()

        emptyList<String>().shouldContainNoNulls()
        listOf(1, 2, 3).shouldContainNoNulls()
        listOf(null, null, null).shouldNotContainNoNulls()
        listOf(1, null, null).shouldNotContainNoNulls()

        shouldThrow<AssertionError> {
          listOf(null, null, null).shouldContainNoNulls()
        }.message.shouldBe("Collection should not contain nulls")

        shouldThrow<AssertionError> {
          listOf(1, 2, 3).shouldNotContainNoNulls()
        }.message.shouldBe("Collection should have at least one null")
      }
      "support type inference for subtypes of collection" {
        val tests = listOf(
                TestSealed.Test1("test1"),
                TestSealed.Test2(2)
        )
        tests should containNoNulls()
        tests.shouldContainNoNulls()
      }
    }

    "containOnlyNulls" should {
      "test that a collection contains only nulls"  {
        emptyList<String>() should containOnlyNulls()
        listOf(null, null, null) should containOnlyNulls()
        listOf(1, null, null) shouldNot containOnlyNulls()
        listOf(1, 2, 3) shouldNot containOnlyNulls()

        listOf(null, 1, 2, 3).shouldNotContainOnlyNulls()
        listOf(1, 2, 3).shouldNotContainOnlyNulls()
        listOf(null, null, null).shouldContainOnlyNulls()
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
      "work with unsorted collections" {
        val actual = listOf(5, 3, 1, 2, 4, 2)
        actual should containsInOrder(3, 2, 2)
      }
      "print errors unambiguously"  {
        shouldThrow<AssertionError> {
          listOf<Number>(1L, 2L) should containsInOrder(listOf<Number>(1, 2))
        }.message shouldBe "[1L, 2L] did not contain the elements [1, 2] in order"
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

        col.shouldContainAll(1, 2, 3)
        col.shouldContainAll(3, 1)
        col.shouldContainAll(3)

        col.shouldNotContainAll(6)
        col.shouldNotContainAll(1, 6)
        col.shouldNotContainAll(6, 1)

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
      "print errors unambiguously"  {
        shouldThrow<AssertionError> {
          listOf<Number>(1, 2).shouldContainAll(listOf<Number>(1L, 2L))
        }.message shouldBe "Collection should contain all of 1L, 2L"
      }
    }

    "startWith" should {
      "test that a list starts with the given collection" {
        val col = listOf(1, 2, 3, 4, 5)
        col.shouldStartWith(listOf(1))
        col.shouldStartWith(listOf(1, 2))
        col.shouldNotStartWith(listOf(2, 3))
        col.shouldNotStartWith(listOf(4, 5))
        col.shouldNotStartWith(listOf(1, 3))
      }
      "print errors unambiguously"  {
        shouldThrow<AssertionError> {
          listOf(1L, 2L) should startWith(listOf(1L, 3L))
        }.message shouldBe "List should start with [1L, 3L]"
      }
    }

    "endWith" should {
      "test that a list ends with the given collection" {
        val col = listOf(1, 2, 3, 4, 5)
        col.shouldEndWith(listOf(5))
        col.shouldEndWith(listOf(4, 5))
        col.shouldNotEndWith(listOf(2, 3))
        col.shouldNotEndWith(listOf(3, 5))
        col.shouldNotEndWith(listOf(1, 2))
      }
      "print errors unambiguously"  {
        shouldThrow<AssertionError> {
          listOf(1L, 2L) should endWith(listOf(1L, 3L))
        }.message shouldBe "List should end with [1L, 3L]"
      }
    }
  }
}

sealed class TestSealed {
  data class Test1(val value: String) : TestSealed()
  data class Test2(val value: Int) : TestSealed()
}
