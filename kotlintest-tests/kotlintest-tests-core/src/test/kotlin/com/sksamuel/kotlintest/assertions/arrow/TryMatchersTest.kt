package com.sksamuel.kotlintest.assertions.arrow

import arrow.core.Try
import io.kotlintest.assertions.arrow.`try`.beFailure
import io.kotlintest.assertions.arrow.`try`.beFailureWithThrowable
import io.kotlintest.assertions.arrow.`try`.beSuccess
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import java.io.IOException

class TryMatchersTest : WordSpec() {

  init {

    "Try shouldBe Success(value)" should {
      "test that a try is a Success with the given value" {

        shouldThrow<AssertionError> {
          Try.Failure<Nothing>(RuntimeException()) should beSuccess("foo")
        }.message shouldBe "Try should be a Success(foo) but was Failure(null)"

        shouldThrow<AssertionError> {
          Try.Success("boo") should beSuccess("foo")
        }.message shouldBe "Try should be Success(foo) but was Success(boo)"

        Try.Success("foo") should beSuccess("foo")
      }
    }

    "Try shouldBe Failure" should {
      "test that a try is a Failure" {
        shouldThrow<AssertionError> {
          Try.Success("foo") should beFailure()
        }.message shouldBe "Try should be a Failure but was Success(foo)"

        Try.Failure<Nothing>(RuntimeException()) should beFailure()
      }

      "test that a try is a Failure with a given throwable" {
        shouldThrow<AssertionError> {
          Try.Success("foo") should beFailureWithThrowable<RuntimeException>()
        }.message shouldBe "Try should be a Failure but was Success(foo)"

        shouldThrow<AssertionError> {
          Try.Failure<Nothing>(RuntimeException()) should beFailureWithThrowable<IOException>()
        }.message shouldBe "Try should be a Failure(${IOException::class}), but was Failure(${RuntimeException::class})"

        Try.Failure<Nothing>(RuntimeException()) should beFailureWithThrowable<RuntimeException>()
        Try.Failure<Nothing>(RuntimeException()) should beFailureWithThrowable<Exception>()
        Try.Failure<Nothing>(Exception()) shouldNot beFailureWithThrowable<java.lang.RuntimeException>()
      }
    }
  }
}

