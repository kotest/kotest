package io.kotlintest.matchers

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
  }
}