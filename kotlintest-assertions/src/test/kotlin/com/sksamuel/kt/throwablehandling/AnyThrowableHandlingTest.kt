package com.sksamuel.kt.throwablehandling

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import io.kotlintest.shouldThrowAnyUnit
import io.kotlintest.specs.FreeSpec

class AnyThrowableHandlingTest : FreeSpec() {

  init {
    onShouldThrowAnyMatcher { shouldThrowAnyMatcher ->
      "Should throw any ($shouldThrowAnyMatcher)" - {
        "Should throw an exception" - {
          "When no exception is thrown in the code" {
            verifyCorrectErrorIsThrown {
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
  }

  private inline fun onShouldThrowAnyMatcher(func: (ShouldThrowAnyMatcher) -> Unit) {
    func(::shouldThrowAny)
    func(::shouldThrowAnyUnit)
  }

  private fun verifyCorrectErrorIsThrown(block: () -> Unit) {
    val thrown = catchThrowable(block)

    thrown.shouldBeInstanceOf<AssertionError>()
    thrown!!.message shouldBe "Expected a throwable, but nothing was thrown."
  }

  private fun verifyReturnsExactly(thrownInstance: Throwable, block: () -> Any?) {
    val actualReturn = block()

    (actualReturn === thrownInstance).shouldBeTrue()
  }
}

private typealias ShouldThrowAnyMatcher = (() -> Unit) -> Throwable
