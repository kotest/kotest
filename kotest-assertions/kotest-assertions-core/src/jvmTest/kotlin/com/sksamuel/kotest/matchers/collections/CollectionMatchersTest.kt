package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.atLeastSize
import io.kotest.matchers.collections.atMostSize
import io.kotest.matchers.collections.beLargerThan
import io.kotest.matchers.collections.beSameSizeAs
import io.kotest.matchers.collections.beSmallerThan
import io.kotest.matchers.collections.contain
import io.kotest.matchers.collections.containDuplicates
import io.kotest.matchers.collections.containNoNulls
import io.kotest.matchers.collections.containNull
import io.kotest.matchers.collections.containOnlyNulls
import io.kotest.matchers.collections.containsInOrder
import io.kotest.matchers.collections.endWith
import io.kotest.matchers.collections.existInOrder
import io.kotest.matchers.collections.haveElementAt
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.collections.monotonicallyDecreasing
import io.kotest.matchers.collections.monotonicallyDecreasingWith
import io.kotest.matchers.collections.monotonicallyIncreasing
import io.kotest.matchers.collections.monotonicallyIncreasingWith
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldBeLargerThan
import io.kotest.matchers.collections.shouldBeMonotonicallyDecreasing
import io.kotest.matchers.collections.shouldBeMonotonicallyDecreasingWith
import io.kotest.matchers.collections.shouldBeMonotonicallyIncreasing
import io.kotest.matchers.collections.shouldBeMonotonicallyIncreasingWith
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.collections.shouldBeSameSizeAs
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldBeSmallerThan
import io.kotest.matchers.collections.shouldBeSorted
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.collections.shouldBeStrictlyDecreasing
import io.kotest.matchers.collections.shouldBeStrictlyDecreasingWith
import io.kotest.matchers.collections.shouldBeStrictlyIncreasing
import io.kotest.matchers.collections.shouldBeStrictlyIncreasingWith
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldContainDuplicates
import io.kotest.matchers.collections.shouldContainNoNulls
import io.kotest.matchers.collections.shouldContainNull
import io.kotest.matchers.collections.shouldContainOnlyNulls
import io.kotest.matchers.collections.shouldEndWith
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.collections.shouldHaveElementAt
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.collections.shouldNotBeMonotonicallyDecreasing
import io.kotest.matchers.collections.shouldNotBeMonotonicallyDecreasingWith
import io.kotest.matchers.collections.shouldNotBeMonotonicallyIncreasing
import io.kotest.matchers.collections.shouldNotBeMonotonicallyIncreasingWith
import io.kotest.matchers.collections.shouldNotBeOneOf
import io.kotest.matchers.collections.shouldNotBeSingleton
import io.kotest.matchers.collections.shouldNotBeSorted
import io.kotest.matchers.collections.shouldNotBeSortedWith
import io.kotest.matchers.collections.shouldNotBeStrictlyDecreasing
import io.kotest.matchers.collections.shouldNotBeStrictlyDecreasingWith
import io.kotest.matchers.collections.shouldNotBeStrictlyIncreasing
import io.kotest.matchers.collections.shouldNotBeStrictlyIncreasingWith
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.collections.shouldNotContainDuplicates
import io.kotest.matchers.collections.shouldNotContainNoNulls
import io.kotest.matchers.collections.shouldNotContainNull
import io.kotest.matchers.collections.shouldNotContainOnlyNulls
import io.kotest.matchers.collections.shouldNotEndWith
import io.kotest.matchers.collections.shouldNotHaveElementAt
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.collections.shouldNotStartWith
import io.kotest.matchers.collections.shouldStartWith
import io.kotest.matchers.collections.singleElement
import io.kotest.matchers.collections.sorted
import io.kotest.matchers.collections.startWith
import io.kotest.matchers.collections.strictlyDecreasing
import io.kotest.matchers.collections.strictlyDecreasingWith
import io.kotest.matchers.collections.strictlyIncreasing
import io.kotest.matchers.collections.strictlyIncreasingWith
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.shouldNotHave
import io.kotest.matchers.throwable.shouldHaveMessage
import java.util.Comparator

class CollectionMatchersTest : WordSpec() {

   private val countdown = (10 downTo 0).toList()
   private val asc = { a: Int, b: Int -> a - b }
   private val desc = { a: Int, b: Int -> b - a }

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
            }.shouldHaveMessage("List [2, 1] should be sorted. Element 2 at index 0 was greater than element 1")

            listOf(1, 2, 6, 9).shouldBeSorted()

            shouldThrow<AssertionError> {
               listOf(2, 1).shouldBeSorted()
            }.shouldHaveMessage("List [2, 1] should be sorted. Element 2 at index 0 was greater than element 1")

            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldNotBeSorted()
            }.shouldHaveMessage("List [1, 2, 3] should not be sorted")
         }

         "restrict items at the error message" {
            val longList = (1..1000).toList()

            shouldThrow<AssertionError> {
               longList.shouldNotBeSorted()
            }.shouldHaveMessage("List [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, ...] and 980 more should not be sorted")
         }
      }

      "shouldBeIncreasing" should {
         "test that a collection is monotonically increasing" {
            listOf(1, 2, 2, 3) shouldBe monotonicallyIncreasing<Int>()
            listOf(6, 5) shouldNotBe monotonicallyIncreasing<Int>()
            listOf(1, 2, 2, 3).shouldBeMonotonicallyIncreasing()
            listOf(6, 5).shouldNotBeMonotonicallyIncreasing()
         }
         "test that a collection is monotonically increasing according to comparator" {
            val comparator = Comparator(desc)
            listOf(3, 2, 2, 1) shouldBe monotonicallyIncreasingWith(comparator)
            listOf(5, 6) shouldNotBe monotonicallyIncreasingWith(comparator)
            listOf(3, 2, 2, 1).shouldBeMonotonicallyIncreasingWith(comparator)
            listOf(5, 6).shouldNotBeMonotonicallyIncreasingWith(comparator)
         }
         "test that a collection is strictly increasing" {
            listOf(1, 2, 3) shouldBe strictlyIncreasing<Int>()
            listOf(1, 2, 2, 3) shouldNotBe strictlyIncreasing<Int>()
            listOf(6, 5) shouldNotBe strictlyIncreasing<Int>()
            listOf(1, 2, 3).shouldBeStrictlyIncreasing()
            listOf(1, 2, 2, 3).shouldNotBeStrictlyIncreasing()
            listOf(6, 5).shouldNotBeStrictlyIncreasing()
         }
         "test that a collection is strictly increasing according to comparator" {
            val comparator = Comparator(desc)
            listOf(3, 2, 1) shouldBe strictlyIncreasingWith(comparator)
            listOf(3, 2, 2, 1) shouldNotBe strictlyIncreasingWith(comparator)
            listOf(5, 6) shouldNotBe strictlyIncreasingWith(comparator)
            listOf(3, 2, 1).shouldBeStrictlyIncreasingWith(comparator)
            listOf(3, 2, 2, 1).shouldNotBeStrictlyIncreasingWith(comparator)
            listOf(5, 6).shouldNotBeStrictlyIncreasingWith(comparator)
         }
      }

      "shouldBeDecreasing" should {
         "test that a collection is monotonically decreasing" {
            listOf(3, 2, 2, -4) shouldBe monotonicallyDecreasing<Int>()
            listOf(5, 6) shouldNotBe monotonicallyDecreasing<Int>()
            listOf(3, 2, 2, -4).shouldBeMonotonicallyDecreasing()
            listOf(5, 6).shouldNotBeMonotonicallyDecreasing()
         }
         "test that a collection is monotonically decreasing according to comparator" {
            val comparator = Comparator(desc)
            listOf(-4, 2, 2, 3) shouldBe monotonicallyDecreasingWith(comparator)
            listOf(6, 5) shouldNotBe monotonicallyDecreasingWith(comparator)
            listOf(-4, 2, 2, 3).shouldBeMonotonicallyDecreasingWith(comparator)
            listOf(6, 5).shouldNotBeMonotonicallyDecreasingWith(comparator)
         }
         "test that a collection is strictly decreasing" {
            listOf(3, 2, -4) shouldBe strictlyDecreasing<Int>()
            listOf(3, 2, 2, -4) shouldNotBe strictlyDecreasing<Int>()
            listOf(5, 6) shouldNotBe strictlyDecreasing<Int>()
            listOf(3, 2, -4).shouldBeStrictlyDecreasing()
            listOf(3, 2, 2, -4).shouldNotBeStrictlyDecreasing()
            listOf(5, 6).shouldNotBeStrictlyDecreasing()
         }
         "test that a collection is strictly decreasing according to comparator" {
            val comparator = Comparator(desc)
            listOf(-4, 2, 3) shouldBe strictlyDecreasingWith(comparator)
            listOf(-4, 2, 2, 3) shouldNotBe strictlyDecreasingWith(comparator)
            listOf(6, 5) shouldNotBe strictlyDecreasingWith(comparator)
            listOf(-4, 2, 3).shouldBeStrictlyDecreasingWith(comparator)
            listOf(-4, 2, 2, 3).shouldNotBeStrictlyDecreasingWith(comparator)
            listOf(6, 5).shouldNotBeStrictlyDecreasingWith(comparator)
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
            }.shouldHaveMessage("Collection should be a single element of 2 but has 1 elements: [1]")

            shouldThrow<AssertionError> {
               listOf(1, 2) shouldBe singleElement(2)
            }.shouldHaveMessage("Collection should be a single element of 2 but has 2 elements: [1, 2]")
         }
      }

      "singleElement with predicate" should {
         "test that a collection contains a single element by given predicate"  {
            listOf(1) shouldHave singleElement { e -> e == 1 }
            listOf(1).shouldHaveSingleElement { e -> e == 1 }

            shouldThrow<AssertionError> {
               listOf(1) shouldHave singleElement { e -> e == 2 }
            }.shouldHaveMessage("Collection should have a single element by a given predicate but has 0 elements: [1]")

            shouldThrow<AssertionError> {
               listOf(2, 2) shouldHave singleElement { e -> e == 2 }
            }.shouldHaveMessage("Collection should have a single element by a given predicate but has 2 elements: [2, 2]")
         }
      }

      "should contain element" should {
         "test that a collection contains an element"  {
            val col = listOf(1, 2, 3)

            col should contain(2)

            shouldThrow<AssertionError> {
               col should contain(4)
            }.shouldHaveMessage("Collection should contain element 4; listing some elements [1, 2, 3]")
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
            }.shouldHaveMessage("Collection of size 3 should be larger than collection of size 4")
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
            }.shouldHaveMessage("Collection of size 4 should be smaller than collection of size 3")
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
            }.shouldHaveMessage("Collection of size 3 should be the same size as collection of size 4")
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
            }.shouldHaveMessage("Collection should not have size 3. Values: [1, 2, 3]")
         }
      }

      "should be singleton" should {
         "pass for collection with a single element" {
            listOf(1).shouldBeSingleton()
         }

         "fail for collection with 0 elements" {
            shouldThrow<AssertionError> {
               listOf<Int>().shouldBeSingleton()
            }.shouldHaveMessage("Collection should have size 1 but has size 0. Values: []")
         }

         "fail for collection with 2+ elements" {
            shouldThrow<AssertionError> {
               listOf(1, 2).shouldBeSingleton()
            }.shouldHaveMessage("Collection should have size 1 but has size 2. Values: [1, 2]")

            shouldThrow<AssertionError> {
               listOf(1, 2, 3, 4).shouldBeSingleton()
            }.shouldHaveMessage("Collection should have size 1 but has size 4. Values: [1, 2, 3, 4]")
         }
      }

      "should be singleton with block" should {
         "pass for collection with a single element" {
            listOf(1).shouldBeSingleton { it shouldBe 1 }
         }

         "fail for collection with 0 elements" {
            shouldThrow<AssertionError> {
               listOf<Int>().shouldBeSingleton { it shouldBe 1 }
            }.shouldHaveMessage("Collection should have size 1 but has size 0. Values: []")
         }

         "fail for collection with a single incorrect elements" {
            shouldThrow<AssertionError> {
               listOf(2).shouldBeSingleton { it shouldBe 1 }
            }.shouldHaveMessage("expected:<1> but was:<2>")
         }

         "fail for collection with 2+ elements" {
            shouldThrow<AssertionError> {
               listOf(1, 2).shouldBeSingleton { it shouldBe 1 }
            }.shouldHaveMessage("Collection should have size 1 but has size 2. Values: [1, 2]")

            shouldThrow<AssertionError> {
               listOf(1, 2, 3, 4).shouldBeSingleton { it shouldBe 1 }
            }.shouldHaveMessage("Collection should have size 1 but has size 4. Values: [1, 2, 3, 4]")
         }
      }

      "should not be singleton" should {
         "pass for collection with 0 elements" {
            listOf<Int>().shouldNotBeSingleton()
         }

         "pass for collection with 2+ elements" {
            listOf(1, 2).shouldNotBeSingleton()
            listOf(1, 2, 3, 4).shouldNotBeSingleton()
         }

         "fail for collection with a single element" {
            shouldThrow<AssertionError> {
               listOf(1).shouldNotBeSingleton()
            }.shouldHaveMessage("Collection should not have size 1. Values: [1]")
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
            }.shouldHaveMessage("Collection should contain at least 4 elements")

            shouldThrow<AssertionError> {
               list shouldHave atLeastSize(4)
            }.shouldHaveMessage("Collection should contain at least 4 elements")

            shouldThrow<AssertionError> {
               list shouldNotHave atLeastSize(2)
            }.shouldHaveMessage("Collection should contain less than 2 elements")
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
            }.shouldHaveMessage("Collection should contain at most 2 elements")

            shouldThrow<AssertionError> {
               list shouldHave atMostSize(2)
            }.shouldHaveMessage("Collection should contain at most 2 elements")

            shouldThrow<AssertionError> {
               list shouldNotHave atMostSize(4)
            }.shouldHaveMessage("Collection should contain more than 4 elements")
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
            }.shouldHaveMessage("Collection should not contain nulls")

            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldNotContainNoNulls()
            }.shouldHaveMessage("Collection should have at least one null")
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

      "existInOrder" should {
         "test that a collection matches the predicates in the given order, duplicates permitted" {
            val col = listOf(1, 1, 2, 2, 3, 3)

            col should existInOrder(
               { it == 1 },
               { it == 2 },
               { it == 3 }
            )
            col should existInOrder({ it == 1 })

            shouldThrow<AssertionError> {
               col should existInOrder(
                  { it == 1 },
                  { it == 2 },
                  { it == 6 }
               )
            }

            shouldThrow<AssertionError> {
               col should existInOrder({ it == 4 })
            }

            shouldThrow<AssertionError> {
               col should existInOrder(
                  { it == 2 },
                  { it == 1 },
                  { it == 3 }
               )
            }
         }
         "work with unsorted collections" {
            val actual = listOf(5, 3, 1, 2, 4, 2)
            actual should existInOrder(
               { it == 3 },
               { it == 2 },
               { it == 2 }
            )
         }
      }



      "Be one of" should {
         "Pass when the element instance is in the list" {
            val foo = Foo("Bar")
            val list = listOf(foo)

            foo shouldBeOneOf list
         }

         "Fail when the element instance is not in the list" {
            val foo1 = Foo("Bar")
            val foo2 = Foo("Booz")

            val list = listOf(foo1)
            shouldThrow<AssertionError> {
               foo2.shouldBeOneOf(list)
            }.shouldHaveMessage("Collection should contain the instance of value, but doesn't.")
         }

         "Fail when there's an equal element, but not the same instance in the list" {
            val foo1 = Foo("Bar")
            val foo2 = Foo("Bar")

            val list = listOf(foo1)
            shouldThrow<AssertionError> {
               foo2 shouldBeOneOf list
            }.shouldHaveMessage("Collection should contain the instance of value, but doesn't.")
         }

         "Fail when the list is empty" {
            val foo = Foo("Bar")

            val list = emptyList<Foo>()
            shouldThrow<AssertionError> {
               foo shouldBeOneOf list
            }.shouldHaveMessage("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
         }
      }

      "Be one of (negative)" should {
         "Fail when the element instance is in the list" {
            val foo = Foo("Bar")
            val list = listOf(foo)

            shouldThrow<AssertionError> {
               foo shouldNotBeOneOf list
            }.shouldHaveMessage("Collection should not contain the instance of value, but does.")
         }

         "Pass when the element instance is not in the list" {
            val foo1 = Foo("Bar")
            val foo2 = Foo("Booz")

            val list = listOf(foo1)
            foo2.shouldNotBeOneOf(list)
         }

         "Pass when there's an equal element, but not the same instance in the list" {
            val foo1 = Foo("Bar")
            val foo2 = Foo("Bar")

            val list = listOf(foo1)
            foo2 shouldNotBeOneOf list
         }

         "Fail when the list is empty" {
            val foo = Foo("Bar")

            val list = emptyList<Foo>()
            shouldThrow<AssertionError> {
               foo shouldNotBeOneOf list
            }.shouldHaveMessage("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
         }

      }

      "Contain any" should {
         "Fail when the list is empty" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldContainAnyOf(emptyList())
            }.shouldHaveMessage("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
         }

         "Pass when one element is in the list" {
            listOf(1, 2, 3).shouldContainAnyOf(1)
         }

         "Pass when all elements are in the list" {
            listOf(1, 2, 3).shouldContainAnyOf(1, 2, 3)
         }

         "Fail when no element is in the list" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldContainAnyOf(4)
            }.shouldHaveMessage("Collection should contain any of 4")
         }
      }

      "Contain any (negative)" should {
         "Fail when the list is empty" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldNotContainAnyOf(emptyList())
            }.shouldHaveMessage("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
         }

         "Pass when no element is present in the list" {
            listOf(1, 2, 3).shouldNotContainAnyOf(4)
         }

         "Fail when one element is in the list" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldNotContainAnyOf(1)
            }.shouldHaveMessage("Collection should not contain any of 1")
         }

         "Fail when all elements are in the list" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldNotContainAnyOf(1, 2, 3)
            }.shouldHaveMessage("Collection should not contain any of 1, 2, 3")
         }
      }

      "Be in" should {
         "Pass when the element is in the list" {
            val foo = Foo("Bar")
            val list = listOf(foo)

            foo shouldBeIn list
         }

         "Fail when the element is not in the list" {
            val foo1 = Foo("Bar")
            val foo2 = Foo("Booz")

            val list = listOf(foo1)
            shouldThrow<AssertionError> {
               foo2.shouldBeIn(list)
            }.shouldHaveMessage("Collection should contain Foo(bar=Booz), but doesn't. Possible values: [Foo(bar=Bar)]")
         }

         "Pass when there's an equal element, but not the same instance in the list" {
            val foo1 = Foo("Bar")
            val foo2 = Foo("Bar")

            val list = listOf(foo1)
            shouldNotThrow<AssertionError> { foo2 shouldBeIn list }
         }

         "Pass when there's an equal element, but not the same instance in the array" {
            val foo1 = Foo("Bar")
            val foo2 = Foo("Bar")

            val list = arrayOf(foo1)
            shouldNotThrow<AssertionError> { foo2 shouldBeIn list }
         }

         "Fail when the list is empty" {
            val foo = Foo("Bar")

            val list = emptyList<Foo>()
            shouldThrow<AssertionError> {
               foo shouldBeIn list
            }.shouldHaveMessage("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
         }
      }

      "Be in (negative)" should {
         "Fail when the element is in the list" {
            val foo = Foo("Bar")
            val list = listOf(foo)

            shouldThrow<AssertionError> {
               foo shouldNotBeIn list
            }.shouldHaveMessage("Collection should not contain Foo(bar=Bar), but does. Forbidden values: [Foo(bar=Bar)]")
         }

         "Pass when the element is not in the list" {
            val foo1 = Foo("Bar")
            val foo2 = Foo("Booz")

            val list = listOf(foo1)
            shouldNotThrow<AssertionError> {
               foo2.shouldNotBeIn(list)
            }
         }

         "Fail when there's an equal element, but not the same instance in the list" {
            val foo1 = Foo("Bar")
            val foo2 = Foo("Bar")

            val list = listOf(foo1)
            shouldThrow<AssertionError> {
               foo2 shouldNotBeIn list
            }.shouldHaveMessage("Collection should not contain Foo(bar=Bar), but does. Forbidden values: [Foo(bar=Bar)]")
         }

         "Fail when the list is empty" {
            val foo = Foo("Bar")

            val list = emptyList<Foo>()
            shouldThrow<AssertionError> {
               foo shouldNotBeIn list
            }.shouldHaveMessage("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
         }
      }
   }
}

private data class Foo(val bar: String)

sealed class TestSealed {
   data class Test1(val value: String) : TestSealed()
   data class Test2(val value: Int) : TestSealed()
}
