package com.sksamuel.kotest.throwablehandling

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldNotThrowUnit
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.assertions.throwables.shouldThrowUnitWithMessage
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
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
                  verifyThrowsWrongExceptionClass(
                     instanceToThrow,
                     FooRuntimeException::class,
                     NullPointerException::class
                  ) {
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

      "Should not throw" - {
         "Should throw an assertion error wrapping the thrown exception" - {
            "When it's an instance of the right class" {
               val thrownException = FooRuntimeException()

               onShouldNotThrowMatcher<FooRuntimeException> { shouldNotThrowMatcher ->
                  verifyThrowsAssertionWrapping(thrownException) {
                     shouldNotThrowMatcher { throw thrownException }
                  }
               }
            }

            "When it's a subclass of the right class" {
               val thrownException = SubException()

               onShouldNotThrowMatcher<ParentException> { shouldNotThrowMatcher ->

                  verifyThrowsAssertionWrapping(thrownException) {
                     shouldNotThrowMatcher { throw thrownException }
                  }
               }
            }
         }

         "Should throw the thrown exception" - {
            "When it's not an instance nor subclass of the right class" {
               val thrownException = FooRuntimeException()

               onShouldNotThrowMatcher<ParentException> { shouldNotThrowMatcher ->
                  verifyThrowsExactly(thrownException) {
                     shouldNotThrowMatcher { throw thrownException }
                  }
               }
            }

            "When it's an instance of the parent class of the right class" {
               val thrownException = ParentException()

               onShouldNotThrowMatcher<SubException> { shouldNotThrowMatcher ->
                  verifyThrowsExactly(thrownException) {
                     shouldNotThrowMatcher { throw thrownException }
                  }
               }
            }
         }


         "Should not throw an exception" - {
            "When no exception is thrown" {
               onShouldNotThrowMatcher<ParentException> { shouldNotThrowMatcher ->
                  val thrown = catchThrowable {
                     shouldNotThrowMatcher { /* Nothing thrown */ }
                  }

                  thrown shouldBe null
               }
            }
         }
      }

      "should throw with message" - {
         "When the correct exception is thrown, but the message is wrong" {
            onShouldThrowWithMessageMatcher<Exception>("foo") { shouldThrowMatcher ->
               verifyThrowsWrongExceptionMessage("foo", "bar") {
                  shouldThrowMatcher { throw Exception("bar") }
               }
            }
         }
         "Exception class type should have priority in assertion" {
            val instanceToThrow = Exception("foo")

            runCatching {
               shouldThrowWithMessage<RuntimeException>("bar") {
                  throw instanceToThrow
               }
            }
               .exceptionOrNull() shouldBe AssertionError("Expected exception java.lang.RuntimeException but a Exception was thrown instead.")
         }
      }
   }

   private fun <T> onShouldThrowWithMessageMatcher(message: String, func: (ShouldThrowMatcher<T>) -> Unit) {
      func { shouldThrowUnitWithMessage(message, it) }
      func { shouldThrowWithMessage(message, it) }
   }

   private inline fun <reified T : Throwable> onShouldThrowMatcher(func: (ShouldThrowMatcher<T>) -> Unit) {
      func(::shouldThrowUnit)
      func { shouldThrow(it) }
   }

   private fun verifyNoExceptionThrownError(expectedClass: KClass<*>, block: () -> Unit) {
      val throwable = catchThrowable(block)

      throwable.shouldBeInstanceOf<AssertionError>()
      throwable.message shouldBe "Expected exception ${expectedClass.qualifiedName} but no exception was thrown."
   }

   private fun verifyThrowsAssertionErrorInstance(assertionErrorInstance: AssertionError, block: () -> Unit) {
      val throwable = catchThrowable(block)
      (throwable === assertionErrorInstance).shouldBeTrue()
   }

   private fun verifyThrowsWrongExceptionClass(
      thrownInstance: Throwable,
      expectedClass: KClass<*>,
      incorrectClass: KClass<*>,
      block: () -> Unit
   ) {
      val throwable = catchThrowable(block)

      throwable.shouldBeInstanceOf<AssertionError>()
      throwable.message shouldBe "Expected exception ${expectedClass.qualifiedName} but a ${incorrectClass.simpleName} was thrown instead."
      (throwable.cause === thrownInstance).shouldBeTrue()
   }

   private fun verifyThrowsWrongExceptionMessage(
      expectedMessage: String,
      actualMessage: String,
      block: () -> Unit
   ) {
      val throwable = catchThrowable(block)

      throwable.shouldBeInstanceOf<AssertionError>()
      throwable.message shouldBe "Expected exception message '$expectedMessage' but was '$actualMessage' instead."
   }

   private fun verifyReturnsExactly(thrownException: Throwable, block: () -> Any?) {
      val actualReturn = block()

      (thrownException === actualReturn).shouldBeTrue()
   }

   private inline fun <reified T : Throwable> onShouldNotThrowMatcher(func: (ShouldNotThrowMatcher<T>) -> Unit) {
      func { shouldNotThrowUnit<T> { it() } }
      func { shouldNotThrow<T>(it) }
   }

   private fun verifyThrowsAssertionWrapping(exception: Throwable, block: () -> Unit) {
      val thrown = catchThrowable(block)

      thrown.shouldBeInstanceOf<AssertionError>()
      thrown.message shouldBe "No exception expected, but a ${exception::class.simpleName} was thrown."
      thrown.cause shouldBeSameInstanceAs exception
   }

   private fun verifyThrowsExactly(exception: Throwable, block: () -> Unit) {
      val thrown = catchThrowable(block)
      thrown shouldBeSameInstanceAs exception
   }
}

private typealias ShouldThrowMatcher<T> = (() -> Unit) -> T
private typealias ShouldNotThrowMatcher<T> = (() -> Unit) -> Unit
