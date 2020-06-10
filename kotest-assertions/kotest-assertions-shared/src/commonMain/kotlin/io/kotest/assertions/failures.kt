package io.kotest.assertions

import io.kotest.assertions.show.Printed
import io.kotest.mpp.stacktraces

data class Expected(val value: Printed)
data class Actual(val value: Printed)

/**
 * Creates the most appropriate error from the given message, wrapping in clue context(s)
 * if any are set.
 */
fun failure(message: String): AssertionError = failure(message, null)

/**
 * Creates an [AssertionError] from the given message, wrapping in clue context(s)
 * if any are set, and setting the cause as [cause] on platforms that supported nested exceptions.
 *
 * If the platform supports stack traces,
 * then the stack is cleaned of `io.kotest` lines.
 */
fun failure(message: String, cause: Throwable?): AssertionError {
   return stacktraces.cleanStackTrace(Exceptions.createAssertionError(clueContextAsString() + message, cause))
}

/**
 * Creates a [Throwable] from expected and actual values, appending clue context(s)
 * if any are set. The error message is generated in the intellij 'diff' format.
 *
 * This function should be used for "comparison" failures, such as "a" shouldBe "b".
 * For other types of errors (eg timeout, or expected exception but none was thrown) prefer
 * the failure methods that take an explicit message.
 *
 * The given values should have already been [Printed] using the Show typeclass.
 *
 * If the platform supports stack traces,
 * then the stack is cleaned of `io.kotest` lines.
 */
fun failure(expected: Expected, actual: Actual, prependMessage: String = ""): Throwable {
   return stacktraces.cleanStackTrace(
      Exceptions.createAssertionError(
         prependMessage + clueContextAsString() + intellijFormatError(expected, actual),
         null,
         expected,
         actual
      )
   )
}

/**
 * Returns a message formatted appropriately for intellij to show a diff.
 *
 * This is the format intellij requires to recognize:
 * https://github.com/JetBrains/intellij-community/blob/5422868682d7eb8511dda02cf615ff375f5b0324/java/java-runtime/src/com/intellij/rt/execution/testFrameworks/AbstractExpectedPatterns.java
 *
 * From the above link:
 * private static final Pattern ASSERT_EQUALS_PATTERN = Pattern.compile("expected:<(.*)> but was:<(.*)>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
 */
fun intellijFormatError(expected: Expected, actual: Actual): String {
   return "expected:<${expected.value.value}> but was:<${actual.value.value}>"
}
