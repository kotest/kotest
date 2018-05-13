package com.sksamuel.kotlintest.assertions.arrow

import arrow.core.Either
import io.kotlintest.assertions.arrow.either.beLeft
import io.kotlintest.assertions.arrow.either.beRight
import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.assertions.arrow.either.shouldNotBeRight
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

class EitherMatchersTest : WordSpec() {

  init {

    "Either should beRight()" should {
      "test that the either is of type right" {
        Either.right("boo").shouldBeRight()
      }
    }

    "Either should beRight(value)" should {
      "test that an either is a right with the given value" {

        shouldThrow<AssertionError> {
          Either.left("foo") should beRight("boo")
        }.message shouldBe "Either should be Right(boo) but was Left(foo)"

        shouldThrow<AssertionError> {
          Either.right("foo") should beRight("boo")
        }.message shouldBe "Either should be Right(boo) but was Right(foo)"

        Either.left("foo").shouldNotBeRight("foo")

        Either.right("boo") should beRight("boo")
        Either.right("boo").shouldBeRight("boo")
      }
    }

    "Either should beLeft()" should {
      "test that the either is of type left" {
        Either.right("boo").shouldBeLeft()
      }
    }

    "Either should beLeft(value)" should {
      "test that an either is a left with the given value" {

        shouldThrow<AssertionError> {
          Either.right("foo") should beLeft("boo")
        }.message shouldBe "Either should be Left(boo) but was Right(foo)"

        shouldThrow<AssertionError> {
          Either.left("foo") should beLeft("boo")
        }.message shouldBe "Either should be Left(boo) but was Left(foo)"

        Either.left("boo") should beLeft("boo")
        Either.left("boo").shouldBeLeft("boo")
      }
    }
  }
}