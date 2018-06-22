package com.sksamuel.kotlintest.matchers.collections

import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.collections.*
import io.kotlintest.matchers.containAll
import io.kotlintest.matchers.containsInOrder
import io.kotlintest.matchers.haveSize
import io.kotlintest.matchers.singleElement
import io.kotlintest.matchers.sorted
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldHave
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotHave
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import java.util.*

class CollectionMatchersTest : WordSpec() {

  init {

    "haveElementAt" should {
      "test that a collection contains the specified element at the given index" {
        listOf("a", "b", "c") should haveElementAt(1, "b")
        listOf("a", "b", "c") shouldNot haveElementAt(1, "c")
        listOf("a", "b", null) should haveElementAt(2, null)

        listOf("a", "b", "c").shouldContainElementAt(1, "b")
        listOf("a", "b", "c").shouldNotContainElementAt(1, "c")
        listOf("a", "b", null).shouldContainElementAt(2, null)
      }
      "support type inference for subtypes of collection" {
        val tests = listOf(
            TestSealed.Test1("test1"),
            TestSealed.Test2(2)
        )
        tests should haveElementAt(0, TestSealed.Test1("test1"))
        tests.shouldContainElementAt(1, TestSealed.Test2(2))
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
    }

    "empty" should {
      "test that a collection contains an element"  {
        val col = listOf(1, 2, 3)

        shouldThrow<AssertionError> {
          col should beEmpty()
        }.message.shouldBe("Collection should be empty")

        shouldThrow<AssertionError> {
          col.shouldBeEmpty()
        }.message.shouldBe("Collection should be empty")

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
    }
  }
}

sealed class TestSealed {
  data class Test1(val value: String) : TestSealed()
  data class Test2(val value: Int) : TestSealed()
}
