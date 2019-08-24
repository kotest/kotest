package com.sksamuel.kotlintest

import com.sksamuel.kotlintest.throwablehandling.catchThrowable
import io.kotlintest.assertions.Failures
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.assertions.shouldFail
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class FailuresTest : FreeSpec() {

  init {
    "Failures.shouldFail" - {
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

    "Failures.failure" - {

      "filters stacktrace" {
        val cause = RuntimeException()
        val failure = Failures.failure("msg", cause)
        failure.message shouldBe "msg"
        failure.cause shouldBe cause
        failure.stackTrace[0].className.shouldStartWith("com.sksamuel.kotlintest.FailuresTest")
      }

      "filters stacktrace when called by shouldBe" {
        val t = shouldThrowAny { 1 shouldBe 2 }
        t.stackTrace[0].className.shouldStartWith("com.sksamuel.kotlintest.FailuresTest")
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
