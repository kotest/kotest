package com.sksamuel.kotest.throwablehandling

import io.kotest.assertions.throwables.shouldNotThrowExactly
import io.kotest.assertions.throwables.shouldNotThrowExactlyUnit
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.throwables.shouldThrowExactlyUnit
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.reflect.KClass

class StrictThrowableHandlingTest : FreeSpec() {

  init {
    "Should throw exactly" - {
      "Should throw a new exception" - {
        "When no exception is thrown" {
          onShouldThrowExactlyMatcher<FooRuntimeException> { shouldThrowExactlyMatcher ->
            verifyNoExceptionThrownError(FooRuntimeException::class) {
              shouldThrowExactlyMatcher { /* No exception is thrown */ }
            }
          }
        }

        "When an exception is thrown, but it's not the right class" {
          val instanceToThrow = NullPointerException()

          onShouldThrowExactlyMatcher<FooRuntimeException> { shouldThrowExactlyMatcher ->
            verifyThrowsWrongExceptionClass(instanceToThrow, FooRuntimeException::class, NullPointerException::class) {
              shouldThrowExactlyMatcher { throw instanceToThrow }
            }
          }
        }

        "When an exception is thrown, but it's the parent class of the right class" {
          val instanceToThrow = ParentException()

          onShouldThrowExactlyMatcher<SubException> { shouldThrowExactlyMatcher ->
            verifyThrowsWrongExceptionClass(instanceToThrow, SubException::class, ParentException::class) {
              shouldThrowExactlyMatcher { throw instanceToThrow }
            }
          }
        }

        "When an exception is thrown, but it's subclass of the right class" {
          val instanceToThrow = SubException()
          onShouldThrowExactlyMatcher<ParentException> { shouldThrowExactlyMatcher ->
            verifyThrowsWrongExceptionClass(instanceToThrow, ParentException::class, SubException::class) {
              shouldThrowExactlyMatcher { throw instanceToThrow }
            }
          }
        }
      }

      "Should throw the thrown exception" - {
        "When an exception is thrown, and it's an Assertion Error (special case) when it's not the expected error" {
          val thrownInstance = AssertionError()

          onShouldThrowExactlyMatcher<FooRuntimeException> { shouldThrowExactlyMatcher ->
            verifyThrowsAssertionErrorInstance(thrownInstance) {
              shouldThrowExactlyMatcher { throw thrownInstance }
            }
          }
        }
      }

      "Should return the thrown exception" - {
        "When an exception is thrown, and it's exactly the right class" {
          val thrownException = FooRuntimeException()
          onShouldThrowExactlyMatcher<FooRuntimeException> { shouldThrowExactlyMatcher ->
            verifyReturnsExactly(thrownException) {
              shouldThrowExactlyMatcher { throw thrownException }
            }
          }
        }

        "When an AssertionError is thrown, and it's exactly the right class" {
          val thrownException = AssertionError()
          onShouldThrowExactlyMatcher<AssertionError> { shouldThrowExactlyMatcher ->
            verifyReturnsExactly(thrownException) {
              shouldThrowExactlyMatcher { throw thrownException }
            }
          }
        }
      }
    }

    "Should not throw exactly" - {
      "Should throw an assertion error wrapping the thrown exception" - {
        "When the exact class is thrown" {
          val thrownException = FooRuntimeException()

          onShouldNotThrowExactlyMatcher<FooRuntimeException> { shouldNotThrowExactlyMatcher ->
            verifyThrowsAssertionWrapping(thrownException) {
              shouldNotThrowExactlyMatcher { throw thrownException }
            }
          }
        }
      }

      "Should throw the thrown exception" - {
        "When it's a subclass of the expected type" {
          val thrownException = SubException()

          onShouldNotThrowExactlyMatcher<ParentException> { shouldNotThrowExactlyMatcher ->
            verifyThrowsExactly(thrownException) {
              shouldNotThrowExactlyMatcher { throw thrownException }
            }
          }
        }

        "When it's a super class of the expected type" {
          val thrownException = ParentException()

          onShouldNotThrowExactlyMatcher<SubException> { shouldNotThrowExactlyMatcher ->
            verifyThrowsExactly(thrownException) {
              shouldNotThrowExactlyMatcher { throw thrownException }
            }
          }
        }

        "When it's unrelated to the expected type" {
          val thrownException = FooRuntimeException()

          onShouldNotThrowExactlyMatcher<ParentException> { shouldNotThrowExactlyMatcher ->
            verifyThrowsExactly(thrownException) {
              shouldNotThrowExactlyMatcher { throw thrownException }
            }
          }
        }
      }

      "Should not throw anything" - {
        "When nothing is thrown" {

          onShouldNotThrowExactlyMatcher<FooRuntimeException> { shouldNotThrowExactlyMatcher ->
            verifyNoErrorIsThrown {
              shouldNotThrowExactlyMatcher { /* Success */ }
            }
          }
        }
      }
    }
  }

  private inline fun <reified T : Throwable> onShouldThrowExactlyMatcher(func: (ShouldThrowExactlyMatcher<T>) -> Unit) {
    func(::shouldThrowExactlyUnit)
    func { shouldThrowExactly(it) }
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

  private inline fun <reified T : Throwable> onShouldNotThrowExactlyMatcher(func: (ShouldNotThrowExactlyMatcher) -> Unit) {
    func { shouldNotThrowExactly<T>(it) }
    func { shouldNotThrowExactlyUnit<T>(it) }
  }

  private fun verifyThrowsAssertionWrapping(thrownException: FooRuntimeException, block: () -> Unit) {
    val thrown = catchThrowable(block)

    thrown!!.shouldBeInstanceOf<AssertionError>()
    thrown.message shouldBe "No exception expected, but a FooRuntimeException was thrown."
    thrown.cause shouldBeSameInstanceAs thrownException

  }

  private fun verifyThrowsExactly(thrownException: Throwable, block: () -> Unit) {
    catchThrowable(block).shouldBeSameInstanceAs(thrownException)
  }

  private fun verifyNoErrorIsThrown(block: () -> Unit) {
    block()
  }

}

private typealias ShouldThrowExactlyMatcher<T> = (() -> Unit) -> T
private typealias ShouldNotThrowExactlyMatcher = (() -> Unit) -> Unit
