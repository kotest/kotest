package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.WordSpec
import io.kotest.equals.Equality
import io.kotest.equals.types.byObjectEquality
import io.kotest.matchers.collections.atLeastSize
import io.kotest.matchers.collections.atMostSize
import io.kotest.matchers.collections.beLargerThan
import io.kotest.matchers.collections.beSameSizeAs
import io.kotest.matchers.collections.beSmallerThan
import io.kotest.matchers.collections.contain
import io.kotest.matchers.collections.containNoNulls
import io.kotest.matchers.collections.containNull
import io.kotest.matchers.collections.containOnlyNulls
import io.kotest.matchers.collections.exist
import io.kotest.matchers.collections.existInOrder
import io.kotest.matchers.collections.haveElementAt
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.collections.matchEach
import io.kotest.matchers.collections.matchInOrder
import io.kotest.matchers.collections.matchInOrderSubset
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldBeLargerThan
import io.kotest.matchers.collections.shouldBeSameSizeAs
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldBeSmallerThan
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldContainNoNulls
import io.kotest.matchers.collections.shouldContainNull
import io.kotest.matchers.collections.shouldContainOnlyNulls
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.collections.shouldHaveElementAt
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldMatchInOrder
import io.kotest.matchers.collections.shouldMatchInOrderSubset
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.collections.shouldNotBeSingleton
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.collections.shouldNotContainNoNulls
import io.kotest.matchers.collections.shouldNotContainNull
import io.kotest.matchers.collections.shouldNotContainOnlyNulls
import io.kotest.matchers.collections.shouldNotHaveElementAt
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.collections.shouldNotMatchEach
import io.kotest.matchers.collections.shouldNotMatchInOrder
import io.kotest.matchers.collections.shouldNotMatchInOrderSubset
import io.kotest.matchers.collections.singleElement
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotHave
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.throwable.shouldHaveMessage

class CollectionMatchersTest : WordSpec() {

   init {
      "haveElementAt" should {
         "test that a collection contains the specified element at the given index" {
            listOf("a", "b", "c") should haveElementAt(1, "b")
            listOf("a", "b", "c") shouldNot haveElementAt(1, "c")
            listOf("a", "b", null) should haveElementAt(2, null)
            listOf("a", "b", null) shouldNot haveElementAt(3, null)

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
         "print if list is too short" {
            shouldThrowAny {
               listOf("a", "b", "c").shouldHaveElementAt(3, "d")
            }.message shouldBe """
            |Collection ["a", "b", "c"] should contain "d" at index 3
            |But it is too short: only 3 elements
            """.trimMargin()
         }
         "print if element does not match" {
            shouldThrowAny {
               listOf("a", "b", "c").shouldHaveElementAt(2, "d")
            }.message shouldBe """
            |Collection ["a", "b", "c"] should contain "d" at index 2
            |Expected: <"c">, but was <"d">
            """.trimMargin()
         }
         "print if element found at another index" {
            shouldThrowAny {
               listOf("a", "b", "c").shouldHaveElementAt(2, "b")
            }.message shouldBe """
            |Collection ["a", "b", "c"] should contain "b" at index 2
            |Expected: <"c">, but was <"b">
            |Element was found at index(es): [1]
            """.trimMargin()
         }
         "print if element found at multiple other indexes" {
            shouldThrowAny {
               listOf("a", "b", "c", "b").shouldHaveElementAt(2, "b")
            }.message shouldBe """
            |Collection ["a", "b", "c", "b"] should contain "b" at index 2
            |Expected: <"c">, but was <"b">
            |Element was found at index(es): [1, 3]
            """.trimMargin()
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

      "singleElement" should {
         "test that a collection contains a single given element" {
            listOf(1) shouldBe singleElement(1)
            listOf(1).shouldHaveSingleElement(1)

            shouldThrow<AssertionError> {
               listOf(1) shouldBe singleElement(2)
            }.shouldHaveMessage("Collection should be a single element containing 2\nexpected:<2> but was:<1>")

            shouldThrow<AssertionError> {
               listOf(1, 2) shouldBe singleElement(2)
            }.shouldHaveMessage("Collection should be a single element of 2 but has 2 elements: [1, 2]. Element found at index(es): [1].")

            shouldThrow<AssertionError> {
               listOf(1, 2) shouldBe singleElement(3)
            }.shouldHaveMessage("Collection should be a single element of 3 but has 2 elements: [1, 2]. Element not found in collection.")
         }
      }

      "singleElement with predicate" should {
         "test that a collection contains a single element by given predicate" {
            listOf(1) shouldHave singleElement { e -> e == 1 }
            listOf(1).shouldHaveSingleElement { e -> e == 1 }

            shouldThrow<AssertionError> {
               listOf(1) shouldHave singleElement { e -> e == 2 }
            }.shouldHaveMessage("Collection should have a single element by a given predicate, but no elements matched, and the whole collection was: [1]")

            shouldThrow<AssertionError> {
               listOf(2, 2) shouldHave singleElement { e -> e == 2 }
            }.shouldHaveMessage("Collection should have a single element by a given predicate, but elements with the following indexes matched: [0, 1], and the whole collection was: [2, 2]")
         }
      }

      "should contain element" should {
         "test that a collection contains an element" {
            val col = listOf(1, 2, 3)

            col should contain(2)
            col should contain(2.0) // uses strict num equality = false

            shouldThrow<AssertionError> {
               col should contain(4)
            }.shouldHaveMessage("Collection should contain element 4 based on object equality; but the collection is [1, 2, 3]")
         }
      }

      "should contain element based on a custom equality object" should {
         "test that a collection contains an element" {
            val col = listOf(1, 2, 3.0)


            val verifier = Equality.byObjectEquality<Number>(strictNumberEquality = true)

            col should contain(2, verifier)
            col should contain(3.0, verifier)

            shouldThrow<AssertionError> {
               col should contain(3, verifier)
            }.shouldHaveMessage("Collection should contain element 3 based on object equality; but the collection is [1, 2, 3.0]")
         }
      }


      "shouldBeLargerThan" should {
         "test that a collection is larger than another collection" {
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
         "test that a collection is smaller than another collection" {
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
         "test that a collection is the same size as another collection" {
            val col1 = listOf(1, 2, 3)
            val col2 = setOf(1, 2, 3)
            val col3 = listOf(1, 2, 3, 4)

            val (_, _, third) = col1 shouldBeSameSizeAs col2
            third.shouldBe(3)
            col1 should beSameSizeAs(col2)
            col1 shouldNot beSameSizeAs(col3)

            shouldThrow<AssertionError> {
               col1.shouldBeSameSizeAs(col3)
            }.shouldHaveMessage("Collection of size 3 should be the same size as collection of size 4")
         }
         "test that an iterable is the same size as another iterable" {
            class Group(val name: String, memberIds: Iterable<Int>) : Iterable<Int> by memberIds

            val group = Group("group 1", listOf(1, 2, 3))
            val col2 = setOf(1, 2, 3)
            val col3 = listOf(1, 2, 3, 4)
            group.shouldBeSameSizeAs(col2).name shouldBe "group 1"

            shouldThrow<AssertionError> {
               group.shouldBeSameSizeAs(col3)
            }.shouldHaveMessage("Collection of size 3 should be the same size as collection of size 4")
         }

      }

      "haveSize" should {
         "test that a collection has a certain size" {
            val col1 = listOf(1, 2, 3)
            col1 should haveSize(3)
            val (first, _, third) = col1.shouldHaveSize(3)
            first shouldBe 1
            third shouldBe 3
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

            booleanArrayOf(true, false)
               .shouldHaveSize(2)
               .shouldNotHaveSize(1)
               .shouldHaveAtLeastSize(1)
               .shouldHaveAtMostSize(10)
               .shouldBeSameSizeAs(booleanArrayOf(false, true))
            byteArrayOf(0x01, 0x02, 0x03)
               .shouldHaveSize(3)
               .shouldNotHaveSize(1)
               .shouldHaveAtLeastSize(2)
               .shouldHaveAtMostSize(10)
               .shouldBeSameSizeAs(byteArrayOf(0x04, 0x05, 0x06))
            charArrayOf('a', 'b', 'c')
               .shouldHaveSize(3)
               .shouldNotHaveSize(5)
               .shouldHaveAtLeastSize(1)
               .shouldHaveAtMostSize(10)
               .shouldBeSameSizeAs(charArrayOf('x', 'y', 'z'))
            shortArrayOf(1, 2, 3, 4)
               .shouldHaveSize(4)
               .shouldNotHaveSize(6)
               .shouldHaveAtLeastSize(3)
               .shouldHaveAtMostSize(10)
               .shouldBeSameSizeAs(shortArrayOf(5, 6, 7, 8))
            intArrayOf(1, 2, 3)
               .shouldHaveSize(3)
               .shouldNotHaveSize(0)
               .shouldHaveAtLeastSize(2)
               .shouldHaveAtMostSize(10)
               .shouldBeSameSizeAs(intArrayOf(4, 5, 6))
            longArrayOf(1, 2, 3, 4, 5)
               .shouldHaveSize(5)
               .shouldNotHaveSize(2)
               .shouldHaveAtLeastSize(4)
               .shouldHaveAtMostSize(10)
               .shouldBeSameSizeAs(longArrayOf(6, 7, 8, 9, 10))
            floatArrayOf(1.0f, 2.0f, 3.0f)
               .shouldHaveSize(3)
               .shouldNotHaveSize(2)
               .shouldHaveAtLeastSize(1)
               .shouldHaveAtMostSize(10)
               .shouldBeSameSizeAs(floatArrayOf(4.0f, 5.0f, 6.0f))
            doubleArrayOf(1.0, 2.0, 3.0)
               .shouldHaveSize(3)
               .shouldNotHaveSize(42)
               .shouldHaveAtLeastSize(1)
               .shouldHaveAtMostSize(10)
               .shouldBeSameSizeAs(doubleArrayOf(4.0, 5.0, 6.0))
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
         "give descriptive message when predicate should not match" {
            shouldThrowAny {
               listOf(1, 2, 3, 2) shouldNot exist { it == 2 }
            }.message shouldBe "Collection [1, 2, 3, 2] should not contain an element that matches the predicate, but elements with the following indexes matched: [1, 3]"
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
            }.shouldHaveMessage("Collection [1, 2, 3] should contain at least 4 elements")

            shouldThrow<AssertionError> {
               list shouldHave atLeastSize(4)
            }.shouldHaveMessage("Collection [1, 2, 3] should contain at least 4 elements")

            shouldThrow<AssertionError> {
               list shouldNotHave atLeastSize(2)
            }.shouldHaveMessage("Collection [1, 2, 3] should contain less than 2 elements")
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
            }.shouldHaveMessage("Collection [1, 2, 3] should contain at most 2 elements")

            shouldThrow<AssertionError> {
               list shouldHave atMostSize(2)
            }.shouldHaveMessage("Collection [1, 2, 3] should contain at most 2 elements")

            shouldThrow<AssertionError> {
               list shouldNotHave atMostSize(4)
            }.shouldHaveMessage("Collection [1, 2, 3] should contain more than 4 elements")
         }
      }



      "containNoNulls" should {
         "test that a collection contains zero nulls" {
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
         "test that a collection contains only nulls" {
            emptyList<String>() should containOnlyNulls()
            listOf(null, null, null) should containOnlyNulls()
            listOf(1, null, null) shouldNot containOnlyNulls()
            listOf(1, 2, 3) shouldNot containOnlyNulls()

            listOf(null, 1, 2, 3).shouldNotContainOnlyNulls()
            listOf(1, 2, 3).shouldNotContainOnlyNulls()
            listOf(null, null, null).shouldContainOnlyNulls()
         }
      }

      "matchInOrder" should {
         "test that a collection matches the assertions in the given order, duplicates permitted" {
            withClue("Gaps not allowed") {
               shouldFail {
                  listOf(1, 2, 2, 3) should matchInOrder(
                     { it shouldBe 1 },
                     { it shouldBe 2 },
                     { it shouldBe 3 }
                  )
               }
            }

            arrayOf(2, 2, 3).shouldMatchInOrder(
               { it shouldBe 2 },
               { it shouldBe 2 },
               { it shouldBe 3 },
            )
         }

         "failure shows best result" {
            shouldFail {
               listOf(1, 2, 3, 1, 2, 1, 2).shouldMatchInOrder(
                  { it shouldBe 1 },
                  { it shouldBe 2 },
                  { it shouldBe 1 },
                  { it shouldBe 3 },
               )
            }.message shouldBe """
               Expected a sequence of elements to pass the assertions, but failed to match all assertions

               Best result when comparing from index [3], where 3 elements passed, but the following elements failed:

               6 => expected:<3> but was:<2>
            """.trimIndent()
         }


         "Non existing element causes error" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldMatchInOrder(
                  { it shouldBe 1 },
                  { it shouldBe 2 },
                  { it shouldBe 6 }
               )
            }
         }

         "out-of-order elements cause error" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3) should matchInOrder(
                  { it shouldBe 2 },
                  { it shouldBe 1 },
                  { it shouldBe 3 }
               )
            }
         }

         "work with unsorted collections" {
            val actual = listOf(5, 3, 1, 2, 4, 2)

            withClue("should match 4th, 5th and 6th elements ([.., 2, 4, 2])") {
               actual should matchInOrder(
                  { it shouldBe 2 },
                  { it shouldBeGreaterThan 3 },
                  { it shouldBeInRange 2..2 }
               )
            }
         }

         "negation should work" {
            shouldFail {
               listOf(1, 2, 3, 4).shouldNotMatchInOrder(
                  { it shouldBe 2 },
                  { it shouldBe 3 },
               )
            }.message shouldBe """
               Expected some assertion to fail but all passed
            """.trimIndent()

            listOf(1, 2, 3, 4).shouldNotMatchInOrder(
               { it shouldBe 2 },
               { it shouldBe 4 }
            )
         }
      }

      "matchInOrderSubset" should {
         "test that a collection matches the assertions in the given order without gaps" {
            listOf(1, 1, 2, 2, 3, 3) should matchInOrderSubset(
               { it shouldBe 1 },
               { it shouldBe 2 },
               { it shouldBe 2 },
               { it shouldBe 3 }
            )

            arrayOf(1, 1, 1).shouldMatchInOrderSubset(
               { it shouldBe 1 }
            )
         }

         "Negation should work" {
            shouldFail {
               listOf(1, 2, 3, 4).shouldNotMatchInOrderSubset(
                  { it shouldBe 2 },
                  { it shouldBe 4 },
               )
            }.message shouldBe """
               Expected some assertion to fail but all passed
            """.trimIndent()

            arrayOf(1, 2, 3, 4).shouldNotMatchInOrder(
               { it shouldBe 4 },
               { it shouldBe 1 }
            )
         }

         "Non existing element causes error" {
            shouldThrow<AssertionError> {
               listOf(1, 1, 2, 2, 3, 3) should matchInOrderSubset(
                  { it shouldBe 1 },
                  { it shouldBe 2 },
                  { it shouldBe 6 }
               )
            }.message shouldBe """
               Expected a sequence of elements to pass the assertions, possibly with gaps between but failed to match all assertions

               Best result when comparing from index [0], where 2 elements passed, but the following elements failed:

               3 => expected:<6> but was:<2>
               4 => expected:<6> but was:<3>
               5 => expected:<6> but was:<3>
            """.trimIndent()
         }

         "out-of-order elements cause error" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3) should matchInOrderSubset(
                  { it shouldBe 2 },
                  { it shouldBe 1 },
                  { it shouldBe 3 }
               )
            }
         }

         "gaps should be ok" {
            listOf(1, 1, 2, 2, 3, 3) should matchInOrderSubset(
               { it shouldBe 1 },
               { it shouldBe 2 },
               { it shouldBe 3 }
            )
         }

         "work with unsorted collections" {
            val actual = listOf(5, 3, 1, 2, 4, 2)

            withClue("should match 4th, 5th and 6th elements ([.., 2, 4, 2])") {
               actual should matchInOrderSubset(
                  { it shouldBe 2 },
                  { it shouldBeGreaterThan 3 },
                  { it shouldBeInRange 2..2 }
               )
            }
         }
      }

      "matchEach" should {
         "test that a collection matches the assertions in the given order without gaps" {
            listOf(1, 3, 7) should matchEach(
               { it shouldBe 1 },
               { it shouldBeInRange 2..4 },
               { it shouldBeGreaterThan 2 }
            )
         }

         "Negation should work" {
            shouldFail {
               listOf(1, 2).shouldNotMatchEach(
                  { it shouldBe 1 },
                  { it shouldBe 2 },
               )
            }.message shouldBe """
               Expected some element to fail its assertion, but all passed.
            """.trimIndent()

            arrayOf(1, 2).shouldNotMatchEach(
               { it shouldBe 2 },
               { it shouldBe 1 }
            )
         }

         "No assertion exists for each element" {
            shouldFail {
               listOf(1, -1, 999) should matchEach(
                  { it shouldBe 1 }
               )
            }.message shouldBe """
               Expected each element to pass its assertion, but found issues at indexes: [1, 2]

               1 => Element has no corresponding assertion. Only 1 assertions provided
               2 => Element has no corresponding assertion. Only 1 assertions provided
            """.trimIndent()
         }

         "Too many assertions cause error" {
            shouldFail {
               listOf(1, 3, 7) should matchEach(
                  { it shouldBe 1 },
                  { it shouldBe 3 },
                  { it shouldBe 7 },
                  { it shouldBe 7 },
                  { it shouldBe 7 },
               )
            }.message shouldBe """
               Expected each element to pass its assertion, but found issues at indexes: [3, 4]

               3 => No actual element for assertion at index 3
               4 => No actual element for assertion at index 4
            """.trimIndent()
         }

         "Non matching element causes error" {
            shouldFail {
               listOf(1, 3, 7) should matchEach(
                  { it shouldBe 1 },
                  { it shouldBeInRange 2..4 },
                  { it shouldBeGreaterThan 7 }
               )
            }.message shouldBe """
               Expected each element to pass its assertion, but found issues at indexes: [2]

               2 => 7 should be > 7
            """.trimIndent()
         }

         "out-of-order elements cause error" {
            shouldThrow<AssertionError> {
               setOf(2, 3, 1) should matchEach(
                  { it shouldBe 2 },
                  { it shouldBe 1 },
                  { it shouldBe 3 }
               )
            }.message shouldBe """
               Expected each element to pass its assertion, but found issues at indexes: [1, 2]

               1 => expected:<1> but was:<3>
               2 => expected:<3> but was:<1>
            """.trimIndent()
         }

         "gaps cause errors" {
            shouldThrow<AssertionError> {
               listOf(1, 1, 2, 2, 3, 3) should matchEach(
                  { it shouldBe 1 },
                  { it shouldBe 2 },
                  { it shouldBe 3 }
               )
            }.message shouldBe """
               Expected each element to pass its assertion, but found issues at indexes: [1, 2, 3, 4, 5]

               1 => expected:<2> but was:<1>
               2 => expected:<3> but was:<2>
               3 => Element has no corresponding assertion. Only 3 assertions provided
               4 => Element has no corresponding assertion. Only 3 assertions provided
               5 => Element has no corresponding assertion. Only 3 assertions provided
            """.trimIndent()
         }
      }

      "matchEach with actual / expected pairs" should {
         "create proper matchers for collections of the same size" {
            shouldThrow<AssertionError> {
               listOf(4, 3, 2, 1) should matchEach(listOf(1, 2, 3, 4)) { actual, expected ->
                  actual shouldBe expected
               }
            }.message shouldBe """
               Expected each element to pass its assertion, but found issues at indexes: [0, 1, 2, 3]

               0 => expected:<1> but was:<4>
               1 => expected:<2> but was:<3>
               2 => expected:<3> but was:<2>
               3 => expected:<4> but was:<1>
            """.trimIndent()
         }

         "element missing on expected list" {
            shouldThrow<AssertionError> {
               listOf(4, 3, 2, 1) should matchEach(listOf(1, 2, 3)) { actual, expected ->
                  actual shouldBe expected
               }
            }.message shouldBe """
               Expected each element to pass its assertion, but found issues at indexes: [0, 1, 2, 3]

               0 => expected:<1> but was:<4>
               1 => expected:<2> but was:<3>
               2 => expected:<3> but was:<2>
               3 => Element has no corresponding assertion. Only 3 assertions provided
            """.trimIndent()
         }

         "element missing on actual list" {
            shouldThrow<AssertionError> {
               listOf(4, 3, 2) should matchEach(listOf(1, 2, 3, 4)) { actual, expected ->
                  actual shouldBe expected
               }
            }.message shouldBe """
               Expected each element to pass its assertion, but found issues at indexes: [0, 1, 2, 3]

               0 => expected:<1> but was:<4>
               1 => expected:<2> but was:<3>
               2 => expected:<3> but was:<2>
               3 => No actual element for assertion at index 3
            """.trimIndent()
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

      "Contain any" should {
         "Fail when the list is empty" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldContainAnyOf(emptyList())
            }.shouldHaveMessage("Asserting content on empty collection. Use Collection.shouldBeEmpty() instead.")
         }

         "Pass when one element is in the list" {
            listOf(1, 2, 3).shouldContainAnyOf(1)
         }

         "Pass when one element is in the iterable" {
            listOf(1, 2, 3).asIterable().shouldContainAnyOf(1)
         }

         "Pass when one element is in the array" {
            arrayOf(1, 2, 3).shouldContainAnyOf(1)
         }

         "Pass when all elements are in the list" {
            listOf(1, 2, 3).shouldContainAnyOf(1, 2, 3)
         }

         "Fail when no element is in the list" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldContainAnyOf(4)
            }.shouldHaveMessage("Collection [1, 2, 3] should contain any of [4]")
         }

         "Fail when no element is in the iterable" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3).asIterable().shouldContainAnyOf(4)
            }.shouldHaveMessage("Collection [1, 2, 3] should contain any of [4]")
         }

         "Fail when no element is in the array" {
            shouldThrow<AssertionError> {
               arrayOf(1, 2, 3).shouldContainAnyOf(4)
            }.shouldHaveMessage("Collection [1, 2, 3] should contain any of [4]")
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
            }.message.shouldContainInOrder(
               "Collection [1, 2, 3] should not contain any of [1]",
               "Forbidden elements found in collection:",
               "[0] => 1",
               )
         }

         "Fail when one element is in the iterable" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3).asIterable().shouldNotContainAnyOf(1)
            }.message.shouldContainInOrder(
               "Collection [1, 2, 3] should not contain any of [1]",
               "Forbidden elements found in collection:",
               "[0] => 1",
            )
         }

         "Fail when one element is in the array" {
            shouldThrow<AssertionError> {
               arrayOf(1, 2, 3).shouldNotContainAnyOf(1)
            }.message.shouldContainInOrder(
               "Collection [1, 2, 3] should not contain any of [1]",
               "Forbidden elements found in collection:",
               "[0] => 1",
               )
         }

         "Fail when all elements are in the list" {
            shouldThrow<AssertionError> {
               listOf(1, 2, 3).shouldNotContainAnyOf(3, 2, 1)
            }.message.shouldContainInOrder(
               "Collection [1, 2, 3] should not contain any of [3, 2, 1]",
               "Forbidden elements found in collection:",
               "[0] => 1",
               "[1] => 2",
               "[2] => 3",
               )
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

         "fail and find similar items" {
            shouldThrow<AssertionError> {
               sweetGreenApple shouldBeIn listOf(
                  sweetGreenPear, sweetRedApple
               )
            }.message.shouldContainInOrder(
               "Possible matches:",
               "expected: Fruit(name=apple, color=green, taste=sweet),",
               "but was: Fruit(name=apple, color=red, taste=sweet),",
               "The following fields did not match:",
               """"color" expected: <"green">, but was: <"red">"""
            )
         }

         "fail and find similar items for Strings" {
            val message = shouldThrow<AssertionError> {
               "sweet green fruit" shouldBeIn listOf(
                  "sweet green pear", "sweet red apple"
               )
            }.message
            message.shouldContainInOrder(
               "Possible matches:",
               """Line[0] ="sweet green pear"""",
               """Match[0]= ++++++++++++----""",
            )
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
