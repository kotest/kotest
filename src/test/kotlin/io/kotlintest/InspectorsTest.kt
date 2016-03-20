package io.kotlintest

import io.kotlintest.matchers.Matchers
import io.kotlintest.matchers.be
import io.kotlintest.specs.WordSpec

class InspectorsTest : WordSpec(), Matchers {

  val list = listOf(1, 2, 3, 4, 5)

  init {

    "forSome" should {
      "pass if one elements pass test" with {
        forSome(list) { t ->
          t shouldBe 3
        }
      }
      "pass if size-1 elements pass test" with {
        forSome(list) { t ->
          t should be gt 1
        }
      }
      "fail if no elements pass test" with {
        expecting(TestFailedException::class) {
          forSome(list) { t ->
            t should be lt 0
          }
        }
      }
      "fail if all elements pass test" with {
        expecting(TestFailedException::class) {
          forSome(list) { t ->
            t should be gt 0
          }
        }
      }
    }

    "forOne" should {
      "pass if one elements pass test" with {
        forOne(list) { t ->
          t shouldBe 3
        }
      }
      "fail if > 1 elements pass test" with {
        expecting(TestFailedException::class) {
          forOne(list) { t ->
            t should be gt 2
          }
        }
      }
      "fail if no elements pass test" with {
        expecting(TestFailedException::class) {
          forOne(list) { t ->
            t shouldBe 22
          }
        }
      }
    }

    "forAny" should {
      "pass if one elements pass test" with {
        forAny(list) { t ->
          t shouldBe 3
        }
      }
      "pass if at least elements pass test" with {
        forAny(list) { t ->
          t should be gt 2
        }
      }
      "fail if no elements pass test" with {
        expecting(TestFailedException::class) {
          forAny(list) { t ->
            t shouldBe 6
          }
        }
      }
    }

    "forExactly" should {
      "pass if exactly k elements pass" with {
        forExactly(2, list) { t ->
          t should be lt 3
        }
      }
      "fail if more elements pass test" with {
        expecting(TestFailedException::class) {
          forExactly(2, list) { t ->
            t should be gt 2
          }
        }
      }
      "fail if less elements pass test" with {
        expecting(TestFailedException::class) {
          forExactly(2, list) { t ->
            t should be lt 2
          }
        }
      }
      "fail if no elements pass test" with {
        expecting(TestFailedException::class) {
          forExactly(2, list) { t ->
            t shouldBe 33
          }
        }
      }
    }
  }
}