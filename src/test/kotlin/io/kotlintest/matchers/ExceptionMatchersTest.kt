package io.kotlintest.matchers

import io.kotlintest.specs.FreeSpec

class ExceptionMatchersTest : FreeSpec() {

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
        } catch (e: AssertionError) {
        }
      }
      "should test for throwables" {
        try {
          shouldThrow<Throwable> {
            throw Throwable("bibble")
          }
          throw RuntimeException("If we get here its a bug")
        } catch (e: AssertionError) {
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
        shouldThrow<IllegalAccessException> {
          throw IllegalAccessException("bibble")
        }
      }
      "error if no exception throw" {
        val result = try {
          shouldThrow<IllegalAccessException> {
            listOf(1, 2, 3)
          }
          true
        } catch (ex: AssertionError) {
          false
        }
        result shouldBe false
      }
      "error if wrong exception throw" {
        val result = try {
          shouldThrow<IllegalAccessException> {
            throw UnsupportedOperationException("bibble")
          }
          true
        } catch (ex: AssertionError) {
          false
        }
        result shouldBe false
      }
    }
  }
}