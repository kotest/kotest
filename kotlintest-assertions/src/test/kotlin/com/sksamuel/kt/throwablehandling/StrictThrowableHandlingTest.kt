package com.sksamuel.kt.throwablehandling

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowExactly
import io.kotlintest.shouldThrowExactlyUnit
import io.kotlintest.specs.FreeSpec
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
}

private typealias ShouldThrowExactlyMatcher<T> = (() -> Unit) -> T