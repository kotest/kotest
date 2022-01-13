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
import io.kotest.matchers.shouldNotBe
import java.io.IOException
import java.lang.AssertionError

class ResultMatchersTest : FreeSpec() {
   data class SuccessfulResultValue(val value: String)

   init {
    "with success result" - {
      "shouldBeSuccess" {
         shouldThrow<AssertionError> {
            runCatching { throw TestException() }.shouldBeSuccess<SuccessfulResultValue>()
         }
         runCatching { SuccessfulResultValue("Test 01") }.shouldBeSuccess { data ->
            // "data" is not nullable here, and thus "value" can be directly accessed.
            data.value shouldBe "Test 01"
         }
         runCatching { SuccessfulResultValue("Test 01").takeUnless { it.value == "Test 01" } }.shouldBeSuccess { data ->
            // "data" is nullable here, and thus "value" access needs a safe call.
            data?.value shouldNotBe "Test 01"
         }
         val r = runCatching { SuccessfulResultValue("Test 01") }
         r.shouldBeSuccess(SuccessfulResultValue("Test 01"))
      }
      "shouldNotBeFailure" {
        Result.success(SuccessfulResultValue("Test 01")).shouldNotBeFailure()
        Result.success(null).shouldNotBeFailure()
      }
      "shouldNotBeSuccess" {
        runCatching { SuccessfulResultValue("Test 01") } shouldNotBeSuccess SuccessfulResultValue("Test 02")
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
