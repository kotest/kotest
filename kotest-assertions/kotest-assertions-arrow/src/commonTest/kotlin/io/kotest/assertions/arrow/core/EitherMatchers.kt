package io.kotest.assertions.arrow.core

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

private sealed interface Failure
private data class HttpError(val code: Int) : Failure
private data object Timeout : Failure
private data class Person(val name: String, val age: Int)

class EitherMatchers : StringSpec({
  "shouldBeRight"{
    checkAll(Arb.int()) { i ->
      Either.Right(i) shouldBeRight i

      shouldThrowWithMessage<AssertionError>("Expected Either.Right, but found Either.Left with value $i") {
        i.left() shouldBeRight i
      }
    }
  }

  "shouldNotBeRight" {
    checkAll(
      Arb.bind(Arb.int(), Arb.int(), ::Pair)
        .filter { (a, b) -> a != b }
    ) { (a, b) ->
      Either.Right(a) shouldNotBeRight b
    }
  }

  "shouldBeLeft"{
    checkAll(Arb.int()) { i ->
      Either.Left(i) shouldBeLeft i

      shouldThrowWithMessage<AssertionError>("Expected Either.Left, but found Either.Right with value $i") {
        i.right() shouldBeLeft i
      }
    }
  }

  "shouldNotBeLeft" {
    checkAll(
      Arb.bind(Arb.int(), Arb.int(), ::Pair)
        .filter { (a, b) -> a != b }
    ) { (a, b) ->
      Either.Left(a) shouldNotBeLeft b
    }
  }

  "shouldBeRight collects clues" {
     shouldThrowWithMessage<AssertionError>(
        "a clue:\nExpected Either.Right, but found Either.Left with value 1",
     ) {
        withClue("a clue:") { 1.left().shouldBeRight() }
     }
  }

  "shouldBeLeft collects clues" {
     shouldThrowWithMessage<AssertionError>(
        "a clue:\nExpected Either.Left, but found Either.Right with value 1",
     ) {
        withClue("a clue:") { 1.right().shouldBeLeft() }
     }
  }

  "shouldBeLeft without type argument still returns the left value" {
     val either: Either<Failure, Int> = HttpError(404).left()
     val failure: Failure = either.shouldBeLeft()
     failure shouldBe HttpError(404)
     either.shouldBeLeft { "custom $it" } shouldBe HttpError(404)
     shouldThrowWithMessage<AssertionError>("custom 1") { 1.right().shouldBeLeft { "custom $it" } }
  }

  "shouldBeRight without type argument still returns the right value" {
     val either: Either<Failure, Int> = 1.right()
     val value: Int = either.shouldBeRight()
     value shouldBe 1
     either.shouldBeRight { "custom $it" } shouldBe 1
     shouldThrowWithMessage<AssertionError>("custom 1") { 1.left().shouldBeRight { "custom $it" } }
  }

  "shouldBeLeft with type argument returns the cast left value" {
     val either: Either<Failure, Int> = HttpError(404).left()
     either.shouldBeLeft<HttpError>().code shouldBe 404
  }

  "shouldBeLeft with type argument fails on wrong subtype" {
     val either: Either<Failure, Int> = Timeout.left()
     shouldThrow<AssertionError> { either.shouldBeLeft<HttpError>() }.message shouldContain "HttpError"
  }

  "shouldBeLeft with type argument fails on right" {
     shouldThrowWithMessage<AssertionError>("Expected Either.Left, but found Either.Right with value 1") {
        1.right().shouldBeLeft<HttpError>()
     }
  }

  "shouldBeLeft with type argument and assertions" {
     val either: Either<Failure, Int> = HttpError(404).left()
     either.shouldBeLeft<HttpError> { it.code shouldBe 404 } shouldBe HttpError(404)
     shouldThrow<AssertionError> {
        either.shouldBeLeft<HttpError> { it.code shouldBe 500 }
     }.message shouldBe "expected:<500> but was:<404>"
  }

  "shouldBeRight with type argument returns the cast right value" {
     val either: Either<Failure, Any> = Person("sam", 30).right()
     either.shouldBeRight<Person>().name shouldBe "sam"
  }

  "shouldBeRight with type argument fails on wrong subtype" {
     val either: Either<Failure, Any> = "sam".right()
     shouldThrow<AssertionError> { either.shouldBeRight<Person>() }.message shouldContain "Person"
  }

  "shouldBeRight with type argument fails on left" {
     shouldThrowWithMessage<AssertionError>("Expected Either.Right, but found Either.Left with value 1") {
        1.left().shouldBeRight<Person>()
     }
  }

  "shouldBeRight with type argument and assertions" {
     val either: Either<Failure, Any> = Person("sam", 30).right()
     either.shouldBeRight<Person> { it.age shouldBe 30 } shouldBe Person("sam", 30)
     shouldThrow<AssertionError> {
        either.shouldBeRight<Person> { it.age shouldBe 31 }
     }.message shouldBe "expected:<31> but was:<30>"
  }
})
