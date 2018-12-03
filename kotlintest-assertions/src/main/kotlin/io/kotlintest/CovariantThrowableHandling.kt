package io.kotlintest


/**
 * Verifies if a block of code throw a Throwable of type [T] or subtypes
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
 * If you don't care about the thrown exception, use [shouldThrowAnyUnit].
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
  val expectedExceptionClass = T::class
  val thrownThrowable = try {
    block()
    null  // Can't throw Failures.failure here directly, as it would be caught by the catch clause, and it's an AssertionError, which is a special case
  } catch (thrown: Throwable) { thrown }

  return when (thrownThrowable) {
    null -> throw Failures.failure("Expected exception ${expectedExceptionClass.qualifiedName} but no exception was thrown.")
    is T -> thrownThrowable               // This should be before `is AssertionError`. If the user is purposefully trying to verify `shouldThrow<AssertionError>{}` this will take priority
    is AssertionError -> throw thrownThrowable
    else -> throw Failures.failure("Expected exception ${expectedExceptionClass.qualifiedName} but a ${thrownThrowable::class.simpleName} was thrown instead.", thrownThrowable)
  }
}