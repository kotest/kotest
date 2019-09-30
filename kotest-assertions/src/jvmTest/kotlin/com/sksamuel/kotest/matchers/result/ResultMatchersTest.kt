package com.sksamuel.kotest.matchers.result

import io.kotest.matchers.beInstanceOf
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeFailureOfType
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.result.shouldNotBeFailure
import io.kotest.matchers.result.shouldNotBeFailureOfType
import io.kotest.matchers.result.shouldNotBeSuccess
import io.kotest.should
import io.kotest.shouldBe
import io.kotest.specs.FreeSpec
import java.io.IOException

class ResultMatchersTest : FreeSpec() {
  init {
    "with success result" - {
      "shouldBeSuccess" - {
        Result.runCatching { "Test 01" }.shouldBeSuccess { data ->
          data shouldBe "Test 01"
        }
        Result.runCatching { "Test 01" } shouldBeSuccess "Test 01"
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
        Result.runCatching { throw TestException() }.shouldBeFailure { error ->
          error should beInstanceOf<TestException>()
        }
      }
      "shouldBeFailureOfType" {
        Result.runCatching { throw TestException() }.shouldBeFailureOfType<TestException>()
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
