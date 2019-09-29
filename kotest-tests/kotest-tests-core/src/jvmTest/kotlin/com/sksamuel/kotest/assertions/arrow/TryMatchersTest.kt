package com.sksamuel.kotest.assertions.arrow

import arrow.core.Try
import io.kotest.assertions.arrow.`try`.beFailure
import io.kotest.assertions.arrow.`try`.beFailureOfType
import io.kotest.assertions.arrow.`try`.beSuccess
import io.kotest.assertions.arrow.`try`.shouldBeFailure
import io.kotest.assertions.arrow.`try`.shouldBeSuccess
import io.kotest.assertions.arrow.`try`.shouldNotBeFailure
import io.kotest.assertions.arrow.`try`.shouldNotBeSuccess
import io.kotest.should
import io.kotest.shouldBe
import io.kotest.shouldNot
import io.kotest.shouldThrow
import io.kotest.specs.WordSpec
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

        Try.Success("foo").shouldNotBeFailure()
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
        Try.Failure(RuntimeException()).shouldNotBeSuccess()
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
        Try.Failure(RuntimeException()) shouldNotBeSuccess Failure("boo")
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
