package io.kotlintest.matchers

import io.kotlintest.TestFailedException
import io.kotlintest.specs.FreeSpec

class ExceptionMatchersTest : FreeSpec(), Matchers {

  init {
    "shouldThrow" - {
      "should test for correct exception" {
        shouldThrow<IllegalAccessException> {
          throw IllegalAccessException("bibble")
        }
      }
      "should test for correct exception" {
        try {
          shouldThrow<IllegalStateException> {
            throw IllegalAccessException("bibble")
          }
          throw RuntimeException("If we get here its a bug")
        } catch (e: TestFailedException) {
        }
      }
      "should test for throwables" {
        try {
          shouldThrow<Throwable> {
            throw Throwable("bibble")
          }
          throw RuntimeException("If we get here its a bug")
        } catch (e: TestFailedException) {
        }
      }
      "return matched exception" {
        val e = shouldThrow<IllegalAccessException> {
          throw IllegalAccessException("bibble")
        }
        e.message shouldBe "bibble"
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