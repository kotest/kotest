package io.kotest.core.test

/**
 * This exception is thrown when a test fails an expect block.
 */
object ExpectFailureException : Exception()

fun TestScope.expect(thunk: () -> Boolean) {
   if (!thunk()) throw ExpectFailureException
}
