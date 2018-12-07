package com.sksamuel.kt.throwablehandling

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowUnit
import io.kotlintest.specs.FreeSpec
import kotlin.reflect.KClass

class CovariantThrowableHandlingTest : FreeSpec() {

  private class AssertionErrorSubclass : AssertionError()

  init {

    "Should throw" - {
      "Should throw a new exception" - {
        "When no exception is thrown" {
          onShouldThrowMatcher<FooRuntimeException> { shouldThrowMatcher ->
            verifyNoExceptionThrownError(FooRuntimeException::class) {
              shouldThrowMatcher { /* No exception is thrown */ }
            }
          }
        }

        "When an exception is thrown, but it's not the right class" {
          val instanceToThrow = NullPointerException()

          onShouldThrowMatcher<FooRuntimeException> { shouldThrowMatcher ->
            verifyThrowsWrongExceptionClass(instanceToThrow, FooRuntimeException::class, NullPointerException::class) {
              shouldThrowMatcher { throw instanceToThrow }
            }
          }
        }

        "When an exception is thrown, but it's the parent class of the right class" {
          val instanceToThrow = ParentException()

          onShouldThrowMatcher<SubException> { shouldThrowMatcher ->
            verifyThrowsWrongExceptionClass(instanceToThrow, SubException::class, ParentException::class) {
              shouldThrowMatcher { throw instanceToThrow }
            }
          }
        }

      }

      "Should throw the thrown exception" - {
        "When an exception is thrown, but it's an Assertion Error (special case) when it's not expected" {
          val thrownInstance = AssertionError()

          onShouldThrowMatcher<FooRuntimeException> { shouldThrowMatcher ->
            verifyThrowsAssertionErrorInstance(thrownInstance) {
              shouldThrowMatcher { throw thrownInstance }
            }
          }
        }
      }

      "Should return the thrown exception" - {
        "When an exception is thrown and it's exactly the right class" {
          val thrownException = FooRuntimeException()
          onShouldThrowMatcher<FooRuntimeException> { shouldThrowMatcher ->
            verifyReturnsExactly(thrownException) {
              shouldThrowMatcher { throw thrownException }
            }
          }
        }

        "When an exception is thrown and it's a subclass of the right class" {
          val thrownException = SubException()
          onShouldThrowMatcher<ParentException> { shouldThrowMatcher ->
            verifyReturnsExactly(thrownException) {
              shouldThrowMatcher { throw thrownException }
            }
          }
        }

        "When an AssertionError is thrown and it's exactly the right class" {
          val thrownException = AssertionError()
          onShouldThrowMatcher<AssertionError> { shouldThrowMatcher ->
            verifyReturnsExactly(thrownException) {
              shouldThrowMatcher { throw thrownException }
            }
          }
        }

        "When a subclass of AssertionError is thrown, and we're expecting an AssertionError" {
          val thrownException = AssertionErrorSubclass()
          onShouldThrowMatcher<AssertionError> { shouldThrowMatcher ->
            verifyReturnsExactly(thrownException) {
              shouldThrowMatcher { throw thrownException }
            }
          }
        }
      }
    }
  }

  private inline fun <reified T : Throwable> onShouldThrowMatcher(func: (ShouldThrowMatcher<T>) -> Unit) {
    func(::shouldThrowUnit)
    func { shouldThrowUnit(it) }
  }

  private fun verifyNoExceptionThrownError(expectedClass: KClass<*>, block: () -> Unit) {
    val throwable = catchThrowable(block)

    throwable.shouldBeInstanceOf<AssertionError>()
    throwable!!.message shouldBe "Expected exception ${expectedClass.qualifiedName} but no exception was thrown."
  }

  private fun verifyThrowsAssertionErrorInstance(assertionErrorInstance: AssertionError, block: () -> Unit) {
    val throwable = catchThrowable(block)
    (throwable === assertionErrorInstance).shouldBeTrue()
  }

  private fun verifyThrowsWrongExceptionClass(thrownInstance: Throwable, expectedClass: KClass<*>, incorrectClass: KClass<*>, block: () -> Unit) {
    val throwable = catchThrowable(block)

    throwable.shouldBeInstanceOf<AssertionError>()
    throwable!!.message shouldBe "Expected exception ${expectedClass.qualifiedName} but a ${incorrectClass.simpleName} was thrown instead."
    (throwable.cause === thrownInstance).shouldBeTrue()
  }

  private fun verifyReturnsExactly(thrownException: Throwable, block: () -> Any?) {
    val actualReturn = block()

    (thrownException === actualReturn).shouldBeTrue()
  }
}

private typealias ShouldThrowMatcher<T> = (() -> Unit) -> T