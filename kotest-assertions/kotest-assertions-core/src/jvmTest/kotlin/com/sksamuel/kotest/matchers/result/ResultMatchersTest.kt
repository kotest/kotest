package com.sksamuel.kotest.matchers.result

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeFailureOfType
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.result.shouldNotBeFailure
import io.kotest.matchers.result.shouldNotBeFailureOfType
import io.kotest.matchers.result.shouldNotBeSuccess
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import java.io.IOException
import java.lang.AssertionError

class ResultMatchersTest : FreeSpec() {
  init {
    "with success result" - {
      "shouldBeSuccess" {
         shouldThrow<AssertionError> {
            runCatching { throw TestException() }.shouldBeSuccess<Unit>()
         }
         runCatching { "Test 01" }.shouldBeSuccess { data ->
            data shouldBe "Test 01"
         }
         val r = runCatching { "Test 01" }
         r.shouldBeSuccess("Test 01")
      }
      "shouldNotBeFailure" {
        Result.success("Test 01").shouldNotBeFailure()
        Result.success(null).shouldNotBeFailure()
      }
      "shouldNotBeSuccess" {
        runCatching { "Test 01" } shouldNotBeSuccess "Test 02"
      }
    }
    "with error result" - {
      "shouldBeFailure" {
        runCatching { throw TestException() }.shouldBeFailure()
        runCatching { throw TestException() }.shouldBeFailure { error ->
          error should beInstanceOf<TestException>()
        }
      }
      "shouldBeFailureOfType" {
        runCatching { throw TestException() }.shouldBeFailureOfType<TestException>()
      }
      "shouldNotBeFailureOfType" {
        runCatching { throw TestException() }.shouldNotBeFailureOfType<IOException>()
      }
      "shouldNotBeSuccess" {
        runCatching { throw TestException() }.shouldNotBeSuccess()
      }
    }
  }

  class TestException : Throwable("Fake exception")
}
