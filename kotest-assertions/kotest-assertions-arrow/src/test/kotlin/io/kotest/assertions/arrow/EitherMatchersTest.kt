package io.kotest.assertions.arrow

import arrow.core.Either
import io.kotest.assertions.arrow.either.beLeft
import io.kotest.assertions.arrow.either.beRight
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeLeftOfType
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.assertions.arrow.either.shouldNotBeLeft
import io.kotest.assertions.arrow.either.shouldNotBeLeftOfType
import io.kotest.assertions.arrow.either.shouldNotBeRight
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

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
      "use contracts to expose Right<*>" {
        val e = Either.right("boo")
        e.shouldBeRight()
        e.b shouldBe "boo"
      }
    }

    "Either should beRight(fn)" should {
      "test that the either is of type right" {
        data class Person(val name: String, val location: String)
        Either.right(Person("sam", "chicago")) shouldBeRight {
          it.name shouldBe "sam"
          it.location shouldBe "chicago"
        }
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

        Either.right("foo") shouldNotBeRight "boo"
        Either.left("foo") shouldNotBeRight "foo"

        Either.right("boo") should beRight("boo")
        Either.right("boo") shouldBeRight "boo"
      }
    }

    "Either should beLeft()" should {
      "test that the either is of type left" {
        Either.left("boo").shouldBeLeft()
        Either.right("boo").shouldNotBeLeft()
      }
      "use contracts to expose Left<*>" {
        val e = Either.left("boo")
        e.shouldBeLeft()
        e.a shouldBe "boo"
      }
    }

    "Either should beLeft(fn)" should {
      "test that the either is of type right" {
        data class Person(val name: String, val location: String)
        Either.left(Person("sam", "chicago")) shouldBeLeft {
          it.name shouldBe "sam"
          it.location shouldBe "chicago"
        }
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
          Either.left("foo") shouldNotBeLeft "foo"
        }.message shouldBe "Either should not be Left(foo)"

        Either.left("boo") should beLeft("boo")
        Either.left("boo") shouldBeLeft "boo"
        Either.right("boo") shouldNotBeLeft "boo"
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
