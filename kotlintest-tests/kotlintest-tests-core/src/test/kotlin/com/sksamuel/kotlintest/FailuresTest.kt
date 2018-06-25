package com.sksamuel.kotlintest

import io.kotlintest.Failures
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.specs.FreeSpec

class FailuresTest : FreeSpec({

  "Failures.failure" - {

    "filters stacktrace" {
      val failure = Failures.failure("msg")
      failure.message shouldBe "msg"
      failure.stackTrace[0].className.shouldStartWith("com.sksamuel.kotlintest.FailuresTest")
    }

    "filters stacktrace when called by shouldBe" {
      shouldThrowAny { 1 shouldBe 2 }
          .stackTrace[0].className.shouldStartWith("com.sksamuel.kotlintest.FailuresTest")
    }
  }
})
