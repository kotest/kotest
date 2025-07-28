package com.sksamuel.kotest.throwablehandling


import io.kotest.assertions.assertSoftly
import io.kotest.assertions.MultiAssertionError
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowAnyUnit
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs

class AnyThrowableHandlingTest : FreeSpec() {

  init {
     onShouldThrowAnyMatcher { shouldThrowAnyMatcher ->
        "Should throw any ($shouldThrowAnyMatcher)" - {
           "Should throw an exception" - {
              "When no exception is thrown in the code" {
                 verifyThrowsNoExceptionError {
                    shouldThrowAnyMatcher { /* No exception being thrown */ }
                 }
              }
           }

           "Should return the thrown instance" - {
              "When an exception is thrown in the code" {
                 val instanceToThrow = FooRuntimeException()

                 verifyReturnsExactly(instanceToThrow) {
                    shouldThrowAnyMatcher { throw instanceToThrow }
                 }
              }
           }
        }
     }

     onShouldNotThrowAnyMatcher { shouldNotThrowAnyMatcher ->
        "Should not throw any($shouldNotThrowAnyMatcher)" - {
           "Should throw an exception" - {
              "When any exception is thrown in the code" {
                 val exception = FooRuntimeException()

                 verifyThrowsAssertionWrapping(exception) {
                    shouldNotThrowAnyMatcher { throw exception }
                 }
              }
           }

           "Should not throw an exception" - {
              "When no exception is thrown in the code" {
                 verifyNoErrorIsThrown {
                    shouldNotThrowAnyMatcher { /* Nothing thrown */ }
                 }
              }
           }
        }
     }

     "shouldNotThrowAnyUnit" - {
        "should collaborate with assertSoftly if a non-kotest assertion fails" {
           val thrown = shouldThrowExactly<MultiAssertionError> {
              assertSoftly {
                 (2 + 2) shouldBe 5
                 shouldNotThrowAnyUnit {
                    failedNonKotestAssertion()
                 }
                 (3 - 2) shouldBe 2
              }
           }
           thrown.message.shouldContainInOrder(
              "The following 3 assertions failed:",
              "1) expected:<5> but was:<4>",
              """2) No exception expected, but a AssertionError was thrown with message: "Non-kotest assertion failure".""",
              "3) expected:<2> but was:<1>",
           )
        }

        "should collaborate with assertSoftly if an exception is thrown" {
           val thrown = shouldThrowExactly<MultiAssertionError> {
              assertSoftly {
                 (2 + 2) shouldBe 5
                 shouldNotThrowAnyUnit {
                    throw Exception("Oops!")
                 }
                 (3 - 2) shouldBe 2
              }
           }
           thrown.message.shouldContainInOrder(
              "The following 3 assertions failed:",
              "1) expected:<5> but was:<4>",
              """2) No exception expected, but a Exception was thrown with message: "Oops!".""",
              "3) expected:<2> but was:<1>",
           )
        }
     }
  }

  private inline fun onShouldThrowAnyMatcher(func: (ShouldThrowAnyMatcher) -> Unit) {
    func(::shouldThrowAny)
    func(::shouldThrowAnyUnit)
  }

  private fun verifyThrowsNoExceptionError(block: () -> Unit) {
    val thrown = catchThrowable(block)

    thrown.shouldBeInstanceOf<AssertionError>()
    thrown.message shouldBe "Expected a throwable, but nothing was thrown."
  }

  private fun verifyReturnsExactly(thrownInstance: Throwable, block: () -> Any?) {
    val actualReturn = block()

    (actualReturn === thrownInstance).shouldBeTrue()
  }

  private inline fun onShouldNotThrowAnyMatcher(func: (ShouldNotThrowAnyMatcher) -> Unit) {
    func(::shouldNotThrowAny)
    func(::shouldNotThrowAnyUnit)
  }

  private fun verifyThrowsAssertionWrapping(throwable: Throwable, block: () -> Any?) {
    val thrownException = catchThrowable(block)

    thrownException.shouldBeInstanceOf<AssertionError>()
    thrownException.message shouldBe "No exception expected, but a ${throwable::class.simpleName} was thrown with message: \"null\"."
    thrownException.cause shouldBeSameInstanceAs throwable
  }

  private fun verifyNoErrorIsThrown(block: () -> Unit) {
    catchThrowable(block) shouldBe null
  }
}

private typealias ShouldThrowAnyMatcher = (() -> Unit) -> Throwable
private typealias ShouldNotThrowAnyMatcher = (() -> Unit) -> Unit

private fun failedNonKotestAssertion() {
   throw AssertionError("Non-kotest assertion failure")
}
