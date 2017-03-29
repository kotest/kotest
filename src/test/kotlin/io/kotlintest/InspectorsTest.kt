package io.kotlintest

import io.kotlintest.matchers.*
import io.kotlintest.specs.WordSpec

class InspectorsTest : WordSpec() {

  val list = listOf(1, 2, 3, 4, 5)
  val array = arrayOf(1, 2, 3, 4, 5)

  init {

    "forNone" should {
      "pass if no elements pass fn test for a list" {
        forNone(list) {
          it shouldBe 10
        }
      }
      "pass if no elements pass fn test for an array" {
        forNone(array) {
          it shouldBe 10
        }
      }
      "fail if one elements passes fn test" {
        shouldThrow<AssertionError> {
          forNone(list) {
            it shouldBe 4
          }
        }
      }
      "fail if all elements pass fn test" {
        shouldThrow<AssertionError> {
          forNone(list) {
            it should beGreaterThan(0)
          }
        }
      }
    }

    "forSome" should {
      "pass if one elements pass test"  {
        forSome(list) {
          it shouldBe 3
        }
      }
      "pass if size-1 elements pass test"  {
        forSome(list) {
          it should beGreaterThan(1)
        }
      }
      "fail if no elements pass test"  {
        shouldThrow<AssertionError> {
          forSome(array) {
            it should beLessThan(0)
          }
        }
      }
      "fail if all elements pass test"  {
        shouldThrow<AssertionError> {
          forSome(list) {
            it should beGreaterThan(0)
          }
        }
      }
    }

    "forOne" should {
      "pass if one elements pass test"  {
        forOne(list) {
          it shouldBe 3
        }
      }
      "fail if > 1 elements pass test"  {
        shouldThrow<AssertionError> {
          forOne(list) { t ->
            t should beGreaterThan(2)
          }
        }
      }
      "fail if no elements pass test"  {
        shouldThrow<AssertionError> {
          forOne(array) { t ->
            t shouldBe 22
          }
        }
      }
    }

    "forAny" should {
      "pass if one elements pass test"  {
        forAny(list) { t ->
          t shouldBe 3
        }
      }
      "pass if at least elements pass test"  {
        forAny(list) { t ->
          t should beGreaterThan(2)
        }
      }
      "fail if no elements pass test"  {
        shouldThrow<AssertionError> {
          forAny(array) { t ->
            t shouldBe 6
          }
        }
      }
    }

    "forExactly" should {
      "pass if exactly k elements pass"  {
        forExactly(2, list) { t ->
          t should beLessThan(3)
        }
      }
      "fail if more elements pass test"  {
        shouldThrow<AssertionError> {
          forExactly(2, list) { t ->
            t should beGreaterThan(2)
          }
        }
      }
      "fail if less elements pass test"  {
        shouldThrow<AssertionError> {
          forExactly(2, array) { t ->
            t should beLessThan(2)
          }
        }
      }
      "fail if no elements pass test"  {
        shouldThrow<AssertionError> {
          forExactly(2, list) { t ->
            t shouldBe 33
          }
        }
      }
    }
  }
}