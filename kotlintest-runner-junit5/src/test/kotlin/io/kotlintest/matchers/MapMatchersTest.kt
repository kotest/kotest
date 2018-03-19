package io.kotlintest.matchers

import io.kotlintest.should
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

    "containAll" should {
      "test that a map contains all given pairs" {
        val map = mapOf(Pair(1, "a"), Pair(2, "b"), Pair(3, "c"))
        map should containAll(mapOf(1 to "a", 3 to "c"))
        shouldThrow<AssertionError> {
          map should containAll(mapOf(3 to "c"))
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
  }
}