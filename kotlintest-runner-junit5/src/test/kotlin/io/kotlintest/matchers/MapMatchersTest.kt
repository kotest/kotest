package io.kotlintest.matchers

import java.util.LinkedList

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

class MapMatchersTest : WordSpec() {

  init {

    "haveKey" should {
      "test that a map contains the given key" {
        val map = mapOf(Pair(1, "a"), Pair(2, "b"))
        map should haveKey(1)
        shouldThrow<AssertionError> {
          map should haveKey(3)
        }
      }
    }

    "haveValue" should {
      "test that a map contains the given value" {
        val map = mapOf(Pair(1, "a"), Pair(2, "b"))
        map should haveValue("a")
        shouldThrow<AssertionError> {
          map should haveValue("c")
        }
      }
    }

    "contain" should {
      "test that a map contains the given pair" {
        val map = mapOf(Pair(1, "a"), Pair(2, "b"))
        map should contain(1, "a")
        shouldThrow<AssertionError> {
          map should contain(2, "a")
        }
      }
    }

    "haveKeys" should {
      "test that a map contains all given keys" {
        val map = mapOf(Pair(1, "a"), Pair(2, "b"), Pair(3, "c"))
        map should haveKeys(1, 3)
        map should haveKeys(1, 2, 3)
        shouldThrow<AssertionError> {
          map should haveKeys(4)
        }
        shouldThrow<AssertionError> {
          map should haveKeys(1, 4)
        }
      }
    }

    "haveValues" should {
      "test that a map contains all given values" {
        val map = mapOf(Pair(1, "a"), Pair(2, "b"), Pair(3, "c"))
        map should haveValues("b", "c")
        map should haveValues("a", "b", "c")
        shouldThrow<AssertionError> {
          map should haveValues("a", "d")
        }
        shouldThrow<AssertionError> {
          map should haveValues("d")
        }
      }
    }

    "containAll" should {
      "test that a map contains all given pairs" {
        val map = mapOf(Pair(1, "a"), Pair(2, "b"), Pair(3, "c"))
        map should containAll(mapOf(1 to "a", 3 to "c"))
        shouldThrow<AssertionError> {
          map should containAll(mapOf(3 to "c"))
        }
      }
      "test empty map" {
        emptyMap<Any, Any>() should containAll(emptyMap<Any, Any>())
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
          mapOf("a" to 1, "b" to 2) shouldNotBe containAll(mapOf("a" to 1))
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
          val arrayList = arrayListOf(1)
          val linkedList = LinkedList<Int>()
          linkedList.push(1)
          mapOf("a" to arrayList) shouldNotBe containExactly(mapOf("a" to linkedList))
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
  }
}