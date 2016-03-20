package io.kotlintest

import io.kotlintest.matchers.Matchers
import io.kotlintest.specs.FreeSpec

class ExceptionMatchersTest : FreeSpec(), Matchers {

  init {
    "throwException" - {
      "should test for presence of exception" with {
        expecting(IllegalAccessException::class) {
          throw IllegalAccessException("bibble")
        }
      }
      "error if no exception throw" with {
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
      "error if wrong exception throw" with {
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