package io.kotest.assertions.throwables

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.failure
import io.kotest.common.reflection.bestName

/**
 * Verifies that a block of code throws a Throwable of type [T], not including subclasses of [T]
 *
 * Use this function to wrap a block of code to verify if it throws a specific throwable [T], when [shouldThrowExactly]
 * can't be used for whatever reason, such as assignment operations (assignments are statements therefore has no return
 * value).
 *
 * This function will exclude subclasses of [T]. For example, if you test for [IllegalArgumentException] and the code block
 * throws [NumberFormatException], the test will fail, as [NumberFormatException] is a subclass of
 * [IllegalArgumentException], but not exactly [IllegalArgumentException].
 *
 * If you wish to include any subclasses, you should use [shouldThrowUnit] instead.
 *
 * If you don't care about the thrown type at all, use [shouldThrowAnyUnit] instead.
 *
 * ```
 *     val thrown: FooException = shouldThrowExactlyUnit<FooException> {
 *         // Code that we expect to throw FooException
 *         throw FooException()
 *     }
 * ```
 *
 * @see [shouldThrowExactly]
 */
inline fun <reified T : Throwable> shouldThrowExactlyUnit(block: () -> Unit): T = shouldThrowExactly { block() }


/**
 * Verifies that a block of code doesn't throw a Throwable of type [T], not including subclasses of [T]
 *
 * Use this function to wrap a block of code that you'd like to verify whether it throws [T] (not including) or not.
 * If [T] is thrown, this will thrown an [AssertionError]. If anything else is thrown, the throwable will be propagated.
 * This is done so that no unexpected error is silently ignored.
 *
 * This should be used when [shouldNotThrowExactly] can't be used, such as when doing assignments (assignments are statements,
 * therefore has no return value).
 *
 * This function won't include subclasses of [T]. For example, if you test for [IllegalArgumentException] and the code block
 * throws [NumberFormatException], propagate the [NumberFormatException] instead of wrapping it in an AssertionError.
 *
 * If you wish to test [T] and subclasses, use [shouldNotThrowUnit].
 *
 * If you don't care about the thrown exception, use [shouldNotThrowAnyUnit]
 *
 * * ```
 *     shouldNotThrowExactlyUnit<FooException> {
 *        throw FooException() // Fails
 *     }
 * ```
 *
 * @see [shouldNotThrowExactly]
 *
 */
inline fun <reified T : Throwable> shouldNotThrowExactlyUnit(block: () -> Unit) = shouldNotThrowExactly<T>(block)

/**
 * Verifies that a block of code throws a Throwable of type [T], not including subclasses of [T]
 *
 * Use this function to wrap a block of code to verify if it throws a specific throwable [T]
 *
 * This function will exclude subclasses of [T]. For example, if you test for [IllegalArgumentException] and the code block
 * throws [NumberFormatException], the test will fail, as [NumberFormatException] is a subclass of
 * [IllegalArgumentException], but not exactly [IllegalArgumentException].
 *
 * If you wish to include any subclasses, you should use [shouldThrow] instead.
 *
 * If you don't care about the thrown type at all, use [shouldThrowAny] instead.
 *
 *
 *
 * **Attention to assignment operations**:
 *
 * When doing an assignment to a variable, the code won't compile, because an assignment is not of type [Any], as required
 * by [block]. If you need to test that an assignment throws a [Throwable], use [shouldThrowExactlyUnit] or it's variations.
 *
 * ```
 *     val thrown: FooException = shouldThrowExactly<FooException> {
 *         // Code that we expect to throw FooException
 *         throw FooException()
 *     }
 * ```
 *
 * @see [shouldThrowExactly]
 */
inline fun <reified T : Throwable> shouldThrowExactly(block: () -> Any?): T {
   assertionCounter.inc()
   val expectedExceptionClass = T::class
   val thrownThrowable = try {
      block()
      null  // Can't throw failure here directly, as it would be caught by the catch clause, and it's an AssertionError, which is a special case
   } catch (thrown: Throwable) {
      thrown
   }

   return when {
      thrownThrowable == null -> throw failure("Expected exception ${T::class.bestName()} but no exception was thrown.")
      thrownThrowable::class == expectedExceptionClass -> thrownThrowable as T  // This should be before `is AssertionError`. If the user is purposefully trying to verify `shouldThrow<AssertionError>{}` this will take priority
      thrownThrowable is AssertionError -> throw thrownThrowable
      else -> throw failure("Expected exception ${expectedExceptionClass.bestName()} but a ${thrownThrowable::class.simpleName} was thrown instead.",
         thrownThrowable)
   }
}


/**
 * Verifies that a block of code doesn't throw a Throwable of type [T], not including subclasses of [T]
 *
 * Use this function to wrap a block of code that you'd like to verify whether it throws [T] (not including) or not.
 * If [T] is thrown, this will thrown an [AssertionError]. If anything else is thrown, the throwable will be propagated.
 * This is done so that no unexpected error is silently ignored.
 *
 *
 * This function won't include subclasses of [T]. For example, if you test for [IllegalArgumentException] and the code block
 * throws [NumberFormatException], propagate the [NumberFormatException] instead of wrapping it in an AssertionError.
 *
 * If you wish to test [T] and subclasses, use [shouldNotThrow].
 *
 * If you don't care about the thrown exception, use [shouldNotThrowAny]
 *
 * **Attention to assignment operations**:
 *
 * When doing an assignment to a variable, the code won't compile, because an assignment is not of type [Any], as required
 * by [block]. If you need to test that an assignment doesn't throw a [Throwable], use [shouldNotThrowExactlyUnit] or it's variations.
 *
 * ```
 *     val thrown: FooException = shouldThrowExactly<FooException> {
 *         // Code that we expect to throw FooException
 *         throw FooException()
 *     }
 * ```
 *
 * @see [shouldNotThrowExactlyUnit]
 *
 */
inline fun <reified T : Throwable> shouldNotThrowExactly(block: () -> Any?) {
   assertionCounter.inc()
   val thrown = try {
      block()
      return
   } catch (t: Throwable) {
      t
   }

   if (thrown::class == T::class) throw failure("No exception expected, but a ${thrown::class.simpleName} was thrown.",
      thrown)
   throw thrown
}
