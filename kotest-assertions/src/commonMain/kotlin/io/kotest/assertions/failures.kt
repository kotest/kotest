package io.kotest.assertions

import io.kotest.assertions.show.Printed

/**
 * JVM only: If [shouldRemoveKotestElementsFromStacktrace] is `true`,
 * the stacktrace will be reduced to the user-code StackTrace only.
 *
 * Other environments will return the throwable as is.
 */
expect fun cleanStackTrace(throwable: Throwable): Throwable

/**
 * Creates the most appropriate error from the given message, wrapping in clue context(s)
 * if any are set.
 */
expect fun failure(message: String): Throwable

/**
 * Creates the most appropriate error from the given message, wrapping in clue context(s)
 * if any are set, and setting the cause as [cause] on platforms that supported nested exceptions.
 */
expect fun failure(message: String, cause: Throwable?): Throwable

/**
 * Creates a comparison error from the expected and actual values, wrapping in clue context(s)
 * if any are set.
 *
 * The given values should have already been [Printed] using the [Show] typeclass.
 */
expect fun failure(expected: Printed, actual: Printed): Throwable

/**
 * Returns a message formatted appropriately for intellij to show a diff.
 *
 * This is the format intellij requires to recognize:
 * https://github.com/JetBrains/intellij-community/blob/5422868682d7eb8511dda02cf615ff375f5b0324/java/java-runtime/src/com/intellij/rt/execution/testFrameworks/AbstractExpectedPatterns.java
 *
 * From the above link:
 * private static final Pattern ASSERT_EQUALS_PATTERN = Pattern.compile("expected:<(.*)> but was:<(.*)>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
 */
fun intellijFormatError(expected: Printed, actual: Printed): String {
   return "expected: ${expected.value} but was: ${actual.value}"
}
