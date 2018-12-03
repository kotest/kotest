package io.kotlintest

/**
 * Verifies that a block of code throws any [Throwable]
 *
 * Use function to wrap a block of code that you want to verify that throws any kind of [Throwable], where using
 * [shouldThrowAny] can't be used for any reason, such as an assignment of a variable (assignments are statements,
 * therefore has no return value).
 *
 * If you want to verify a specific [Throwable], use [shouldThrowExactlyUnit].
 *
 * If you want to verify a [Throwable] and any subclass, use [shouldThrowUnit].
 *
 * @see [shouldThrowAny]
 */
inline fun shouldThrowAnyUnit(block: () -> Unit) = shouldThrowAny(block)

/**
 * Verifies that a block of code throws any [Throwable]
 *
 * Use function to wrap a block of code that you want to verify that throws any kind of [Throwable].
 *
 * If you want to verify a specific [Throwable], use [shouldThrowExactly].
 *
 * If you want to verify a [Throwable] and any subclasses, use [shouldThrow]
 *
 *
 * **Attention to assignment operations:**
 *
 * When doing an assignment to a variable, the code won't compile, because an assignment is not of type [Any], as required
 * by [block]. If you need to test that an assignment throws a [Throwable], use [shouldThrowAnyUnit] or it's variations.
 *
 * ```
 *     val thrownThrowable: Throwable = shouldThrowAny {
 *         throw FooException() // This is a random Throwable, could be anything
 *     }
 * ```
 *
 * @see [shouldThrowAnyUnit]
 */
inline fun shouldThrowAny(block: () -> Any?): Throwable {
  val thrownException = try {
    block()
    null
  } catch (e: Throwable) { e }

  return thrownException ?: throw Failures.failure("Expected a throwable, but nothing was thrown.")
}