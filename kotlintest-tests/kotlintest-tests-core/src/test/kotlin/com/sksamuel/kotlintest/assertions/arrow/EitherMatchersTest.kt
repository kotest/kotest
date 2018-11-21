package com.sksamuel.kotlintest.assertions.arrow

import arrow.core.Either
import io.kotlintest.assertions.arrow.either.*
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

class EitherMatchersTest : WordSpec() {

  sealed class MyError {
    object Foo : MyError()
    object Boo : MyError()
  }

  init {

    "Either should beRight()" should {
      "test that the either is of type right" {
        Either.right("boo").shouldBeRight()
        Either.left("boo").shouldNotBeRight()
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

        Either.right("foo").shouldNotBeRight("boo")
        Either.left("foo").shouldNotBeRight("foo")

        Either.right("boo") should beRight("boo")
        Either.right("boo").shouldBeRight("boo")
      }
    }

    "Either should beLeft()" should {
      "test that the either is of type left" {
        Either.left("boo").shouldBeLeft()
        Either.right("boo").shouldNotBeLeft()
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

        shouldThrow<AssertionError> {
          Either.left("foo").shouldNotBeLeft("foo")
        }.message shouldBe "Either should not be Left(foo)"

        Either.left("boo") should beLeft("boo")
        Either.left("boo").shouldBeLeft("boo")
        Either.right("boo").shouldNotBeLeft("boo")
      }
    }

    "Either should beLeftOfType" should {
      "test that an either is a left have exactly the same type" {
        shouldThrow<AssertionError> {
          Either.left(MyError.Boo).shouldBeLeftOfType<MyError.Foo>()
        }.message shouldBe "Either should be Left<${MyError.Foo::class.qualifiedName}> but was Left<${MyError.Boo::class.qualifiedName}>"

        Either.left(MyError.Foo).shouldBeLeftOfType<MyError.Foo>()
        Either.left(MyError.Boo).shouldBeLeftOfType<MyError.Boo>()

        Either.left(MyError.Boo).shouldNotBeLeftOfType<MyError.Foo>()
        Either.right("foo").shouldNotBeLeftOfType<MyError.Foo>()
      }
    }
  }
}