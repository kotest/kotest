package io.kotest.assertions.arrow.fx.coroutines

import arrow.fx.coroutines.ExitCase
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CancellationException

class ExitCaseTest : StringSpec({
  "shouldBeCancelled" {
    ExitCase.Cancelled(CancellationException("")).shouldBeCancelled()

     shouldThrowWithMessage<AssertionError>("Expected ExitCase.Cancelled, but found ExitCase.Completed") {
        ExitCase.Completed.shouldBeCancelled()
     }

     checkAll(Arb.string()) { s ->
        shouldThrowWithMessage<AssertionError>(
           "Expected ExitCase.Cancelled, but found Failure(failure=java.lang.RuntimeException: $s)",
        ) {
           ExitCase.Failure(RuntimeException(s)).shouldBeCancelled()
        }
     }
  }

  "shouldBeCancelled(e)" {
    checkAll(Arb.string().map { CancellationException(it) }) { e ->
      ExitCase.Cancelled(e).shouldBeCancelled(e)
    }
  }

  "shouldBeCompleted" {
    ExitCase.Completed.shouldBeCompleted()

     checkAll(Arb.string()) { s ->
        shouldThrowWithMessage<AssertionError>(
           "Expected ExitCase.Completed, but found Cancelled(exception=java.util.concurrent.CancellationException: $s)",
        ) {
           ExitCase.Cancelled(CancellationException(s)).shouldBeCompleted()
        }

        shouldThrowWithMessage<AssertionError>(
           "Expected ExitCase.Cancelled, but found Failure(failure=java.lang.RuntimeException: $s)",
        ) {
           ExitCase.Failure(RuntimeException(s)).shouldBeCancelled()
        }
     }
  }

  "shouldBeFailure" {
    checkAll(Arb.string().map(::RuntimeException)) { e ->
      ExitCase.Failure(e).shouldBeFailure()
    }

     shouldThrowWithMessage<AssertionError>("Expected ExitCase.Failure, but found ExitCase.Completed") {
        ExitCase.Completed.shouldBeFailure()
     }

     checkAll(Arb.string()) { s ->
        shouldThrowWithMessage<AssertionError>(
           "Expected ExitCase.Failure, but found Cancelled(exception=java.util.concurrent.CancellationException: $s)",
        ) {
           ExitCase.Cancelled(CancellationException(s)).shouldBeFailure()
        }
     }
  }

  "shouldBeFailure(e)" {
    checkAll(Arb.string().map(::RuntimeException)) { e ->
      ExitCase.Failure(e).shouldBeFailure(e)
    }
  }

   "shouldBeCancelled collects clues" {
      shouldThrowWithMessage<AssertionError>("a clue:\nExpected ExitCase.Cancelled, but found ExitCase.Completed") {
         withClue("a clue:") { ExitCase.Completed.shouldBeCancelled() }
      }

      shouldThrowWithMessage<AssertionError>("a clue:\nExpected ExitCase.Cancelled, but found Failure(failure=java.lang.RuntimeException: an error)") {
         withClue("a clue:") { ExitCase.Failure(RuntimeException("an error")).shouldBeCancelled() }
      }
   }

   "shouldBeCompleted collects clues" {
      shouldThrowWithMessage<AssertionError>(
         "a clue:\nExpected ExitCase.Completed, but found Cancelled(exception=java.util.concurrent.CancellationException: an error)",
      ) {
         withClue("a clue:") { ExitCase.Cancelled(CancellationException("an error")).shouldBeCompleted() }
      }

      shouldThrowWithMessage<AssertionError>(
         "a clue:\nExpected ExitCase.Completed, but found Failure(failure=java.lang.RuntimeException: an error)",
      ) {
         withClue("a clue:") { ExitCase.Failure(RuntimeException("an error")).shouldBeCompleted() }
      }
   }

   "shouldBeFailure collects clues" {
      shouldThrowWithMessage<AssertionError>(
         "a clue:\nExpected ExitCase.Failure, but found ExitCase.Completed",
      ) {
         withClue("a clue:") { ExitCase.Completed.shouldBeFailure() }
      }

      shouldThrowWithMessage<AssertionError>(
         "a clue:\nExpected ExitCase.Failure, but found Cancelled(exception=java.util.concurrent.CancellationException: an error)",
      ) {
         withClue("a clue:") { ExitCase.Cancelled(CancellationException("an error")).shouldBeFailure() }
      }
   }
})
