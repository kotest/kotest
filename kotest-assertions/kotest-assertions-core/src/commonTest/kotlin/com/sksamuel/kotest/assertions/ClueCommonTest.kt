package com.sksamuel.kotest.assertions

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

class ClueCommonTest : FunSpec({

   test("withClue should include clue in message when fail is called with no expected/actual values") {
      val error = shouldThrow<AssertionError> {
         withClue("a clue") {
            AssertionErrorBuilder.fail("error message")
         }
      }
      error.message shouldBe "a clue\nerror message"
   }

   test("withClue should include clue in message when assertion fails with expected and actual values") {
      val error = shouldThrow<AssertionError> {
         withClue("a clue") {
            "actual" shouldBe "expected"
         }
      }
      error.message!! shouldStartWith "a clue\n"
   }

   test("nested withClue should include all clues in message when fail is called") {
      val error = shouldThrow<AssertionError> {
         withClue("outer clue") {
            withClue("inner clue") {
               AssertionErrorBuilder.fail("error message")
            }
         }
      }
      error.message shouldBe "outer clue\ninner clue\nerror message"
   }

})
