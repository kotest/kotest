package io.kotest.assertions.throwables

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.failure
import io.kotest.mpp.bestName

/**
 * Verifies if a block of code throws a Throwable of type [T] or subtypes
 *
 * Use this function to wrap a block of code that you'd like to verify whether it throws [T] (or subclasses) or not.
 *
 * This should be used when [shouldThrow] can't be used, such as when doing assignments (assignments are statements,
 * therefore has no return value).
 *
 * This function will include all subclasses of [T]. For example, if you test for [java.io.IOException] and
 * the code block throws [java.io.FileNotFoundException], the test will pass.
 *
 * If you wish to test for a specific class strictly (excluding subclasses), use [shouldThrowExactlyUnit] instead.
 *
 * If you don't care about the thrown exception type, use [shouldThrowAnyUnit].
 *
 *
 * ```
 *     val thrownException: FooException = shouldThrowUnit<FooException> {
 *         // Code that we expect to throw FooException
 *         throw FooException()
 *     }
 * ```
 *
 * @see [shouldThrow]
 */
inline fun <reified T : Throwable> shouldThrowUnit(block: () -> Unit): T = shouldThrow { block() }

/**
 * Verifies that a block of code doesn't throw a Throwable of type [T] or subtypes
 *
 * Use this function to wrap a block of code that you'd like to verify whether it throws [T] (or subclasses) or not.
 * If [T] is thrown, this will thrown an [AssertionError]. If anything else is thrown, the throwable will be propagated.
 * This is done so that no unexpected error is silently ignored.
 *
 * This should be used when [shouldNotThrow] can't be used, such as when doing assignments (assignments are statements,
 * therefore has no return value).
 *
 * This function will include all subclasses of [T]. For example, if you test for [java.io.IOException] and the code block
 * throws [java.io.FileNotFoundException], this will also throw an AssertionError instead of propagating the [java.io.FileNotFoundException]
 * directly.
 *
 * If you wish to test for a specific class strictly (excluding subclasses), use [shouldNotThrowExactlyUnit] instead.
 *
 * If you don't care about the thrown exception type, use [shouldNotThrowAnyUnit].
 *
 * ```
 *     shouldNotThrowUnit<FooException> {
 *        throw FooException() // Fails
 *     }
 * ```
 *
 * @see [shouldNotThrow]
 *
 */
inline fun <reified T : Throwable> shouldNotThrowUnit(block: () -> Unit) = shouldNotThrow<T>(block)

/**
 * Verifies if a block of code will throw a Throwable of type [T] or subtypes
 *
 * Use this function to wrap a block of code that you'd like to verify whether it throws [T] (or subclasses) or not.
 *
 * This function will include subclasses of [T]. For example, if you test for [java.io.IOException] and
 * the code block throws [java.io.FileNotFoundException], the test will pass.
 *
 * If you wish to test for a specific class strictly (excluding subclasses), use [shouldThrowExactly] instead.
 *
 * If you don't care about the thrown exception, use [shouldThrowAny].
 *
 * **Attention to assignment operations**:
 *
 * When doing an assignment to a variable, the code won't compile, because an assignment is not of type [Any], as required
 * by [block]. If you need to test that an assignment throws a [Throwable], use [shouldThrowUnit] or it's variations.
 *
 * ```
 *     val thrownException: FooException = shouldThrow<FooException> {
 *         // Code that we expect to throw FooException
 *         throw FooException()
 *     }
 * ```
 *
 * @see [shouldThrowUnit]
 */
inline fun <reified T : Throwable> shouldThrow(block: () -> Any?): T {
   assertionCounter.inc()
   val expectedExceptionClass = T::class
   val thrownThrowable = try {
      block()
      null  // Can't throw failure here directly, as it would be caught by the catch clause, and it's an AssertionError, which is a special case
   } catch (thrown: Throwable) {
      thrown
   }

   return when (thrownThrowable) {
      null -> throw failure("Expected exception ${expectedExceptionClass.bestName()} but no exception was thrown.")
      is T -> thrownThrowable               // This should be before `is AssertionError`. If the user is purposefully trying to verify `shouldThrow<AssertionError>{}` this will take priority
      is AssertionError -> throw thrownThrowable
      else -> throw failure("Expected exception ${expectedExceptionClass.bestName()} but a ${thrownThrowable::class.simpleName} was thrown instead.",
         thrownThrowable)
   }
}

/**
 * Verifies that a block of code will not throw a Throwable of type [T] or subtypes
 *
 * Use this function to wrap a block of code that you'd like to verify whether it throws [T] (or subclasses) or not.
 * If [T] is thrown, this will thrown an [AssertionError]. If anything else is thrown, the throwable will be propagated.
 * This is done so that no unexpected error is silently ignored.
 *
 * This function will include all subclasses of [T]. For example, if you test for [java.io.IOException] and the code block
 * throws [java.io.FileNotFoundException], this will also throw an AssertionError instead of propagating the [java.io.FileNotFoundException]
 * directly.
 *
 * If you wish to test for a specific class strictly (excluding subclasses), use [shouldNotThrowExactly] instead.
 *
 * If you don't care about the thrown exception, use [shouldNotThrowAny].
 *
 * **Attention to assignment operations**:
 *
 * When doing an assignment to a variable, the code won't compile, because an assignment is not of type [Any], as required
 * by [block]. If you need to test that an assignment doesn't throw a [Throwable], use [shouldNotThrowUnit] or it's variations.
 *
 * ```
 *     val thrownException: FooException = shouldThrow<FooException> {
 *         throw FooException() // Fails
 *     }
 * ```
 *
 * @see [shouldNotThrowUnit]
 */
inline fun <reified T : Throwable> shouldNotThrow(block: () -> Any?) {
   assertionCounter.inc()
   val thrown = try {
      block()
      return
   } catch (e: Throwable) {
      e
   }

   if (thrown is T)
      throw failure("No exception expected, but a ${thrown::class.simpleName} was thrown.", thrown)
   throw thrown
}
