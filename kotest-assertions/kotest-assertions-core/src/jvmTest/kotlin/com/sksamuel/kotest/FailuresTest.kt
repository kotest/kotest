package com.sksamuel.kotest

import com.sksamuel.kotest.throwablehandling.catchThrowable
import io.kotest.assertions.failure
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf

class FailuresTest : FreeSpec() {

  init {
    "shouldFail" - {
      "Should throw an exception when code succeeds" {
        val thrown = catchThrowable { shouldFail { /* Code succeeds */ } }
        thrown.shouldBeFailureWithNoAssertionErrorThrown()
      }

      "Should throw an exception when code throws something other than an assertion error" {
        val thrown = catchThrowable { shouldFail { throw Exception() } }
        thrown.shouldBeFailureWithWrongExceptionThrown()
      }

      "Should not thrown an exception when code fails with an assertion error" {
        val thrown = catchThrowable { shouldFail { throw AssertionError() } }
        thrown shouldBe null
      }
    }

    "failure" - {

      "filters stacktrace" {
        val cause = RuntimeException()
        val failure = failure("msg", cause)
        failure.message shouldBe "msg"
        failure.cause shouldBe cause
        failure.stackTrace[0].className.shouldStartWith("com.sksamuel.kotest.FailuresTest")
      }

      "filters stacktrace when called by shouldBe" {
        val t = shouldThrowAny { 1 shouldBe 2 }
        t.stackTrace[0].className.shouldStartWith("com.sksamuel.kotest.FailuresTest")
      }
    }
  }

  private fun Throwable?.shouldBeFailureWithNoAssertionErrorThrown() {
    shouldBeInstanceOf<AssertionError>()
    this!!.message shouldBe "Expected exception java.lang.AssertionError but no exception was thrown."
  }

  private fun Throwable?.shouldBeFailureWithWrongExceptionThrown() {
    shouldBeInstanceOf<AssertionError>()
    this!!.message shouldBe "Expected exception java.lang.AssertionError but a Exception was thrown instead."
  }
}
