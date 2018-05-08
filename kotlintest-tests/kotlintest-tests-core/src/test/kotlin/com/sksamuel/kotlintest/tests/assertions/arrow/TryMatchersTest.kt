package com.sksamuel.kotlintest.tests.assertions.arrow

import arrow.core.Try
import io.kotlintest.assertions.arrow.`try`.beFailure
import io.kotlintest.assertions.arrow.`try`.beSuccess
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

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
    }
  }
}