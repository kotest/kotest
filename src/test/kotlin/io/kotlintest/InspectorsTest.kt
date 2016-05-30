package io.kotlintest

import io.kotlintest.matchers.Matchers
import io.kotlintest.matchers.be
import io.kotlintest.specs.WordSpec

class InspectorsTest : WordSpec(), Matchers {

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
        shouldThrow<TestFailedException> {
          forNone(list) {
            it shouldBe 4
          }
        }
      }
      "fail if all elements pass fn test" {
        shouldThrow<TestFailedException> {
          forNone(list) {
            it should be gt 0
          }
        }
      }
    }

    "forSome" should {
      "pass if one elements pass test"  {
        forSome(list) { t ->
          t shouldBe 3
        }
      }
      "pass if size-1 elements pass test"  {
        forSome(list) { t ->
          t should be gt 1
        }
      }
      "fail if no elements pass test"  {
        shouldThrow<TestFailedException> {
          forSome(array) { t ->
            t should be lt 0
          }
        }
      }
      "fail if all elements pass test"  {
        shouldThrow<TestFailedException> {
          forSome(list) { t ->
            t should be gt 0
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
        expecting(TestFailedException::class) {
          forOne(list) { t ->
            t should be gt 2
          }
        }
      }
      "fail if no elements pass test"  {
        expecting(TestFailedException::class) {
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
          t should be gt 2
        }
      }
      "fail if no elements pass test"  {
        shouldThrow<TestFailedException> {
          forAny(array) { t ->
            t shouldBe 6
          }
        }
      }
    }

    "forExactly" should {
      "pass if exactly k elements pass"  {
        forExactly(2, list) { t ->
          t should be lt 3
        }
      }
      "fail if more elements pass test"  {
        shouldThrow<TestFailedException> {
          forExactly(2, list) { t ->
            t should be gt 2
          }
        }
      }
      "fail if less elements pass test"  {
        shouldThrow<TestFailedException> {
          forExactly(2, array) { t ->
            t should be lt 2
          }
        }
      }
      "fail if no elements pass test"  {
        expecting(TestFailedException::class) {
          forExactly(2, list) { t ->
            t shouldBe 33
          }
        }
      }
    }
  }
}