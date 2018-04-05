package com.sksamuel.kotlintest.tests.assertions.arrow

import arrow.core.Either
import io.kotlintest.assertions.arrow.either.left
import io.kotlintest.assertions.arrow.either.right
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

class EitherMatchersTest : WordSpec() {

  init {

    "Either shouldBe right(value)" should {
      "test that an either is a right with the given value" {
        shouldThrow<AssertionError> {
          Either.left("foo") shouldBe right("boo")
        }.message shouldBe "Either should be Right(boo) but was Left(foo)"
        shouldThrow<AssertionError> {
          Either.right("foo") shouldBe right("boo")
        }.message shouldBe "Either should be Right(boo) but was Right(foo)"
        Either.right("boo") shouldBe right("boo")
      }
    }

    "Either shouldBe left(value)" should {
      "test that an either is a left with the given value" {
        shouldThrow<AssertionError> {
          Either.right("foo") shouldBe left("boo")
        }.message shouldBe "Either should be Left(boo) but was Right(foo)"
        shouldThrow<AssertionError> {
          Either.left("foo") shouldBe left("boo")
        }.message shouldBe "Either should be Left(boo) but was Left(foo)"
        Either.left("boo") shouldBe left("boo")
      }
    }
  }
}