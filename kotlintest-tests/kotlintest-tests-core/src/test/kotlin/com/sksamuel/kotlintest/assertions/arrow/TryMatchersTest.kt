package com.sksamuel.kotlintest.assertions.arrow

import arrow.core.Try
import io.kotlintest.assertions.arrow.`try`.*
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
          Try.Failure(RuntimeException()) should beSuccess("foo")
        }.message shouldBe "Try should be a Success(foo) but was Failure(null)"

        shouldThrow<AssertionError> {
          Try.Success("boo") should beSuccess("foo")
        }.message shouldBe "Try should be Success(foo) but was Success(boo)"

        Try.Success("foo") should beSuccess("foo")

        Try.Success("foo") shouldBeSuccess "foo"
        Try.Success("foo") shouldBeSuccess { it shouldBe "foo" }
      }

      "use contracts to expose Success<*>" {
        val t = Try { "boo" }
        t.shouldBeSuccess()
        t.value shouldBe "boo"
      }
    }

    "Try shouldBe Failure" should {
      data class Failure(override val message: String) : RuntimeException(message)

      "test that a try is a Failure" {
        shouldThrow<AssertionError> {
          Try.Success("foo") should beFailure()
        }.message shouldBe "Try should be a Failure but was Success(foo)"

        Try.Failure(RuntimeException()) should beFailure()
      }

      "test that a try is a Failure with a given throwable" {
        shouldThrow<AssertionError> {
          Try.Success("foo") should beFailureOfType<RuntimeException>()
        }.message shouldBe "Try should be a Failure but was Success(foo)"

        shouldThrow<AssertionError> {
          Try.Failure(RuntimeException()) should beFailureOfType<IOException>()
        }.message shouldBe "Try should be a Failure(${IOException::class}), but was Failure(${RuntimeException::class})"

        Try.Failure(RuntimeException()) should beFailureOfType<RuntimeException>()
        Try.Failure(RuntimeException()) should beFailureOfType<Exception>()
        Try.Failure(Exception()) shouldNot beFailureOfType<RuntimeException>()

        Try.Failure(Failure("boo")) shouldBeFailure Failure("boo")
        Try.Failure(Failure("boo")) shouldBeFailure { it.message shouldBe "boo" }
      }
      "use contracts to expose Failure" {
        val t = Try.Failure(Failure("boo"))
        t.shouldBeFailure()
        t.exception shouldBe Failure("boo")
      }
    }
  }
}
