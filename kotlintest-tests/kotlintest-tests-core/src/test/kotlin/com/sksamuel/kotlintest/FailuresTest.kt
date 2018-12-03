package com.sksamuel.kotlintest

import com.sksamuel.kotlintest.throwablehandling.catchThrowable
import io.kotlintest.Failures
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldFail
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class FailuresTest : FreeSpec({

  "Failures.shouldFail" - {
    "Should throw an exception when code succeeds" {
      val thrown = catchThrowable { shouldFail { /* Code succeeds */ } }
      thrown.shouldBeInstanceOf<AssertionError>()
      thrown!!.message shouldBe "This block should fail by throwing a throwable, but not nothing was thrown."
    }

    "Should not thrown an exception when code fails" {
      val thrown = catchThrowable { shouldFail { throw Exception() } }
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
})
