package io.kotest.assertions.arrow.core

import arrow.core.Ior
import arrow.core.leftIor
import arrow.core.rightIor
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class IorMatchers : StringSpec({
  "shouldBeRight"{
    checkAll(Arb.int()) { i ->
      Ior.Right(i) shouldBeRight i

      shouldThrowWithMessage<AssertionError>("Expected Ior.Right, but found Left") {
        Ior.Left(i) shouldBeRight i
      }

      shouldThrowWithMessage<AssertionError>("Expected Ior.Right, but found Both") {
        Ior.Both(i, i) shouldBeRight i
      }
    }
  }

  "shouldNotBeRight" {
    checkAll(
      Arb.bind(Arb.int(), Arb.int(), ::Pair)
        .filter { (a, b) -> a != b }
    ) { (a, b) ->
      Ior.Right(a) shouldNotBeRight b
    }
  }

  "shouldBeLeft"{
    checkAll(Arb.int()) { i ->
      Ior.Left(i) shouldBeLeft i

      shouldThrowWithMessage<AssertionError>("Expected Ior.Left, but found Right") {
        Ior.Right(i) shouldBeLeft i
      }

      shouldThrowWithMessage<AssertionError>("Expected Ior.Left, but found Both") {
        Ior.Both(i, i) shouldBeLeft i
      }
    }
  }

  "shouldNotBeLeft" {
    checkAll(
      Arb.bind(Arb.int(), Arb.int(), ::Pair)
        .filter { (a, b) -> a != b }
    ) { (a, b) ->
      Ior.Left(a) shouldNotBeLeft b
    }
  }

  "shouldBeBoth" {
    checkAll(Arb.int(), Arb.string()) { i,j ->
      val ior = Ior.Both(i,j)
      ior.shouldBeBoth()
      ior.leftValue shouldBe i
      ior.rightValue shouldBe j

      shouldThrowWithMessage<AssertionError>("Expected ior to be a Both, but was: Left") {
        Ior.Left(i).shouldBeBoth()
      }

      shouldThrowWithMessage<AssertionError>("Expected ior to be a Both, but was: Right") {
        Ior.Right(i).shouldBeBoth()
      }
    }
  }

   "shouldBeRight collects clues" {
      shouldThrowWithMessage<AssertionError>("a clue:\nExpected Ior.Right, but found Left") {
         withClue("a clue:") { 1.leftIor().shouldBeRight() }
      }
   }

   "shouldBeLeft collects clues" {
      shouldThrowWithMessage<AssertionError>("a clue:\nExpected Ior.Left, but found Right") {
         withClue("a clue:") { 1.rightIor().shouldBeLeft() }
      }
   }

   "shouldBeBoth collects clues" {
      shouldThrowWithMessage<AssertionError>("a clue:\nExpected ior to be a Both, but was: Right") {
         withClue("a clue:") { 1.rightIor().shouldBeBoth() }
      }
   }
})
