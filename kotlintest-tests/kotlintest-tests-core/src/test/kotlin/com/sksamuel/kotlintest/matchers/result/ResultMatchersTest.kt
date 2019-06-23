package com.sksamuel.kotlintest.matchers.result

import io.kotlintest.matchers.result.*
import io.kotlintest.should
import io.kotlintest.shouldNot
import io.kotlintest.specs.FreeSpec
import java.io.IOException

class ResultMatchersTest: FreeSpec() {
  init {
    "with success result" - {
      "shouldBeSuccess" - {
        Result.runCatching { "Test 01" }.also { result ->
          result.shouldBeSuccess()
          result shouldBeSuccess "Test 01"
        }
      }
      "shouldNotBeFailure" - {
        Result.success("Test 01").shouldNotBeFailure()
      }
      "shouldNotBeSuccess" {
        Result.runCatching { "Test 01" } shouldNotBeSuccess "Test 02"
      }
    }
    "with error result" - {
      "shouldBeFailure" - {
        Result.runCatching { throw TestException() }.shouldBeFailure()
      }
      "shouldBeFailureOfType" {
        Result.runCatching { throw TestException() }.shouldNotBeFailureOfType<TestException>()
      }
      "shouldNotBeFailureOfType" {
        Result.runCatching { throw TestException() }.shouldNotBeFailureOfType<IOException>()
      }
      "shouldNotBeSuccess" - {
        Result.runCatching { throw TestException() }.shouldNotBeSuccess()
      }
    }
  }

  class TestException : Throwable("Fake exception")
}