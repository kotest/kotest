package com.sksamuel.kotest.matchers.maps

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.and
import io.kotest.matchers.maps.*
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import java.util.LinkedList

class MapMatchersTest : WordSpec() {

   init {

      "haveKey" should {
         "test that a map contains the given key" {
            val map = mapOf(Pair(1, "a"), Pair(2, "b"))
            map should haveKey(1)
            map.shouldContainKey(1)
            map.shouldNotContainKey(4)
            shouldThrow<AssertionError> {
               map should haveKey(3)
            }
            shouldThrow<AssertionError> {
               map.shouldContainKey(5)
            }.message.shouldBe("Map should contain key 5")
            shouldThrow<AssertionError> {
               map.shouldNotContainKey(1)
            }.message.shouldBe("Map should not contain key 1")
         }
         "support maps and mutable maps" {
            val mutable = mutableMapOf("a" to "b")
            mutable.shouldHaveKey("a")
            val map = mapOf("a" to "b")
            map.shouldHaveKey("a")
         }
      }

      "haveValue" should {
         "test that a map contains the given value" {
            val map = mapOf(Pair(1, "a"), Pair(2, "b"))
            map should haveValue("a")
            map.shouldContainValue("a")
            map.shouldNotContainValue("A")
            shouldThrow<AssertionError> {
               map should haveValue("c")
            }
            shouldThrow<AssertionError> {
               map.shouldContainValue("c")
            }.message.shouldBe("Map should contain value c")
         }
      }

      "contain" should {
         "test that a map contains the given pair" {
            val map = mapOf(Pair(1, "a"), Pair(2, "b"))
            map should contain(1, "a")
            map.shouldContain(2, "b")
            map.shouldNotContain(3, "A")
            map shouldContain (1 to "a")
            map shouldNotContain (3 to "A")
            shouldThrow<AssertionError> {
               map.shouldContain(1, "c")
            }.message.shouldBe("Map should contain mapping 1=c but was 1=a")
            shouldThrow<AssertionError> {
               map.shouldContain(4, "e")
            }.message.shouldBe("Map should contain mapping 4=e but was {1=a, 2=b}")
            shouldThrow<AssertionError> {
               map should contain(2, "a")
            }.message.shouldBe("Map should contain mapping 2=a but was 2=b")
         }
      }

      "haveKeys" should {
         "test that a map contains all given keys" {
            val map = mapOf(Pair(1, "a"), Pair(2, "b"), Pair(3, "c"))
            map should haveKeys(1, 3)
            map should haveKeys(1, 2, 3)
            map.shouldContainKeys(1, 3)
            map.shouldContainKeys(1, 2, 3)
            shouldThrow<AssertionError> {
               map should haveKeys(4)
            }
            shouldThrow<AssertionError> {
               map should haveKeys(1, 4)
            }
            shouldThrow<AssertionError> {
               map.shouldContainKeys(1, 4)
            }.message.shouldBe("Map did not contain the keys 1, 4")
         }
      }

      "haveValues" should {
         "test that a map contains all given values" {
            val map = mapOf(Pair(1, "a"), Pair(2, "b"), Pair(3, "c"))
            map should haveValues("b", "c")
            map should haveValues("a", "b", "c")
            map.shouldContainValues("b", "c")
            map.shouldContainValues("a", "b", "c")
            shouldThrow<AssertionError> {
               map should haveValues("a", "d")
            }
            shouldThrow<AssertionError> {
               map should haveValues("d")
            }
            shouldThrow<AssertionError> {
               map.shouldContainValues("d")
            }.message.shouldBe("Map did not contain the values d")
            shouldThrow<AssertionError> {
               map.shouldNotContainValues("a", "b")
            }.message.shouldBe("Map should not contain the values a, b")
         }
      }

      "containAll" should {
         "test that a map contains all given pairs" {
            val map = mapOf(Pair(1, "a"), Pair(2, "b"), Pair(3, "c"))
            map should containAll(mapOf(1 to "a", 3 to "c"))
            map should containAll(mapOf(3 to "c"))
            map.shouldContainAll(mapOf(1 to "a", 3 to "c"))
            map.shouldNotContainAll(mapOf(1 to "a", 3 to "h"))
         }
         "test empty map" {
            emptyMap<Any, Any>() should containAll(emptyMap<Any, Any>())
            emptyMap<Any, Any>().shouldContainAll(emptyMap<Any, Any>())
         }
         "test assertion that map does not contain entries from the given map" {
            val e = shouldThrow<AssertionError> {
               emptyMap<Any, Any>() should containAll(mapOf<Any, Any>("\$a" to 1))
            }
            e.message shouldBe """
          |
          |Expected:
          |  mapOf()
          |should contain all of:
          |  mapOf("\${'$'}a" to 1)
          |but differs by:
          |  missing keys:
          |    "\${'$'}a"
          |
        """.trimMargin()
         }
         "test when map contains extra entries" {
            mapOf("a" to 1, "b" to 2) should (
               containAll(mapOf("a" to 1)) and containAll(mapOf("b" to 2)))
         }
         "test assertion when map contains different value type" {
            val e = shouldThrow<AssertionError> {
               mapOf("a" to 1) should containAll(mapOf<String, Any>("a" to 1L))
            }
            e.message shouldBe """
          |
          |Expected:
          |  mapOf("a" to 1)
          |should contain all of:
          |  mapOf("a" to 1L)
          |but differs by:
          |  different values:
          |    "a":
          |      expected:
          |        1L
          |      but was:
          |        1
          |
        """.trimMargin()
         }
         "test that a map with nested map contains all entries from the given map" {
            val map = mapOf("a" to mapOf("b" to 2))
            map should containAll(mapOf("a" to mapOf("b" to 2)))
            val e = shouldThrow<AssertionError> {
               map should containAll(mapOf("a" to mapOf("b" to 3)))
            }
            e.message shouldBe """
          |
          |Expected:
          |  mapOf("a" to mapOf("b" to 2))
          |should contain all of:
          |  mapOf("a" to mapOf("b" to 3))
          |but differs by:
          |  different values:
          |    "a":
          |      different values:
          |        "b":
          |          expected:
          |            3
          |          but was:
          |            2
          |
        """.trimMargin()
         }
         "test shouldNot assertion" {
            val e = shouldThrow<AssertionError> {
               mapOf("a" to 1, "b" to 2) shouldNot containAll(mapOf("a" to 1))
            }
            e.message shouldBe """
          |
          |Expected:
          |  mapOf("a" to 1, "b" to 2)
          |should not contain all of:
          |  mapOf("a" to 1)
          |but contains
          |
        """.trimMargin()
         }
      }

      "containExactly" should {
         "test empty map" {
            emptyMap<Any, Any>() should containExactly(emptyMap<Any, Any>())
            emptyMap<Any, Any>().shouldContainExactly(emptyMap<Any, Any>())
         }
         "test assertion that a map contains extra keys" {
            val e = shouldThrow<AssertionError> {
               mapOf("a" to 1) should containExactly(emptyMap<String, Any>())
            }
            e.message shouldBe """
          |
          |Expected:
          |  mapOf("a" to 1)
          |should be equal to:
          |  mapOf()
          |but differs by:
          |  extra keys:
          |    "a"
          |
        """.trimMargin()
         }
         "test shouldNot assertion" {
            val e = shouldThrow<AssertionError> {
               val arrayList: List<Int> = arrayListOf(1)
               val linkedList = LinkedList<Int>()
               linkedList.push(1)
               mapOf("a" to arrayList) shouldNot containExactly<String, List<Int>>(mapOf("a" to linkedList))
            }
            e.message shouldBe """
          |
          |Expected:
          |  mapOf("a" to listOf(1))
          |should not be equal to:
          |  mapOf("a" to listOf(1))
          |but equals
          |
        """.trimMargin()
         }
      }
      "be empty" should {
         "Work on an empty map" {
            emptyMap<String, String>().shouldBeEmpty()
         }

         "Fail on a non empty map" {
            shouldThrow<AssertionError> {
               mapOf("Foo" to "Bar").shouldBeEmpty()
            }
         }
      }

      "Not be empty" should {
         "Fail on an empty map" {
            shouldThrow<AssertionError> {
               emptyMap<String, String>().shouldNotBeEmpty()
            }
         }

         "Pass on a non empty map" {
            mapOf("Foo" to "Bar").shouldNotBeEmpty()
         }
      }

      "haveSize" should {
         "test that a map has given size" {
            val map = mapOf(1 to "a", 2 to "b")
            map should haveSize(2)
            map shouldHaveSize (2)
            shouldThrow<AssertionError> {
               map should haveSize(3)
            }
            shouldThrow<AssertionError> {
               map shouldHaveSize (3)
            }
         }
      }

      "map comparision" should {
         "give formatted error in case of failure assertion error with shouldBeEqualTo" {
            val map1 = mapOf(
               "Key1" to "Val11",
               "Key2" to "Val21",
               "Key3" to "Val31",
               "Key4" to "Val41",
               "Key5" to "Val51",
               "Key6" to "Val61",
               "Key7" to "Val71",
               "Key8" to "Val81",
               "Key9" to "Val91",
               "Key10" to "Val101",
               "Key11" to "Val111",
               "Key12" to "Val121"
            )

            val map2 = mapOf(
               "Key1" to "Val11",
               "Key2" to "Val22",
               "Key3" to "Val32",
               "Key4" to "Val42",
               "Key5" to "Val52",
               "Key6" to "Val62",
               "Key7" to "Val72",
               "Key8" to "Val82",
               "Key9" to "Val92",
               "Key10" to "Val102",
               "Key11" to "Val112",
               "Key12" to "Val122"
            )

            val expectedErrorMessage = """Expected
{
  "Key1" = "Val11",
  "Key2" = "Val22",
  "Key3" = "Val32",
  "Key4" = "Val42",
  "Key5" = "Val52",
  "Key6" = "Val62",
  "Key7" = "Val72",
  "Key8" = "Val82",
  "Key9" = "Val92",
  "Key10" = "Val102",
...
}
to be equal to
{
  "Key1" = "Val11",
  "Key2" = "Val21",
  "Key3" = "Val31",
  "Key4" = "Val41",
  "Key5" = "Val51",
  "Key6" = "Val61",
  "Key7" = "Val71",
  "Key8" = "Val81",
  "Key9" = "Val91",
  "Key10" = "Val101",
...
}
Values differed at keys Key2, Key3, Key4, Key5, Key6, Key7, Key8, Key9, Key10, Key11, ...""".trimMargin()

            val assertionError = shouldThrow<AssertionError> { map1 shouldBe map2 }

            assertionError.message shouldBe expectedErrorMessage
         }
      }
   }
}
