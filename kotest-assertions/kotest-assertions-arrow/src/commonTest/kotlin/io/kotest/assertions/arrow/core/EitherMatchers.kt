package io.kotest.assertions.arrow.core

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

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
})
