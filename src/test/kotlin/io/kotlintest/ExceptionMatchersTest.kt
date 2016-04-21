package io.kotlintest

import io.kotlintest.matchers.Matchers
import io.kotlintest.specs.FreeSpec

class ExceptionMatchersTest : FreeSpec(), Matchers {

  init {
    "shouldThrow" - {
      "should test for correct exception" {
        shouldThrow<IllegalAccessException> {
          throw IllegalAccessException("bibble")
        }
      }
    }
    "expecting" - {
      "should test for presence of exception" {
        expecting(IllegalAccessException::class) {
          throw IllegalAccessException("bibble")
        }
      }
      "error if no exception throw" {
        val result = try {
          expecting(IllegalAccessException::class) {
            listOf(1, 2, 3)
          }
          true
        } catch (ex: TestFailedException) {
          false
        }
        result shouldBe false
      }
      "error if wrong exception throw" {
        val result = try {
          expecting(IllegalAccessException::class) {
            throw UnsupportedOperationException("bibble")
          }
          true
        } catch (ex: TestFailedException) {
          false
        }
        result shouldBe false
      }
    }
  }
}