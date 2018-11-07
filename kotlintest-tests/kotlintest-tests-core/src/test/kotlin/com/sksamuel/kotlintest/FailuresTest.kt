package com.sksamuel.kotlintest

import io.kotlintest.Failures
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class FailuresTest : FreeSpec({

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
