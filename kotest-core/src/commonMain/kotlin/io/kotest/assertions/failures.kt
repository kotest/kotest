package io.kotest.assertions

import io.kotest.assertions.show.Printed

data class Expected(val value: Printed)
data class Actual(val value: Printed)

/**
 * Removes io.kotest stack elements from the given throwable if the platform supports stack traces.
 */
expect fun <T : Throwable> cleanStackTrace(throwable: T): T

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
   return cleanStackTrace(createAssertionError(clueContextAsString() + message, cause))
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
fun failure(expected: Expected, actual: Actual): Throwable {
   return cleanStackTrace(
      createAssertionError(
         clueContextAsString() + intellijFormatError(expected, actual),
         null,
         expected,
         actual
      )
   )
}

/**
 * Creates an [AssertionError] from the given message. If the platform supports nested exceptions, the cause
 * is set to the given [cause]. If the platform supports stack traces, then the stack is cleaned of `io.kotest`
 * lines.
 */
expect fun createAssertionError(message: String, cause: Throwable?): AssertionError

/**
 * Creates the best error type supported on the platform (eg opentest4j.AssertionFailedException) from the
 * given message and expected and actual values. If the platform supports nested exceptions, the cause
 * is set to the given [cause].
 *
 * If the platform has jUnit4 or jUnit5 on the classpath, it will use exceptions from those platforms.
 */
expect fun createAssertionError(message: String, cause: Throwable?, expected: Expected, actual: Actual): Throwable

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
