package io.kotest.assertions

import io.kotest.assertions.print.Printed
import io.kotest.matchers.clueContextAsString
import org.opentest4j.AssertionFailedError

/**
 * Creates an [AssertionError] from the given message and expected and actual values
 * using the opentest4j library.
 *
 * The exception type is
 * See https://ota4j-team.github.io/opentest4j/docs/1.3.0/api/org/opentest4j/AssertionFailedError.html
 *
 */
actual fun createAssertionError(
   message: String?,
   cause: Throwable?,
   expected: Expected?,
   actual: Actual?
): AssertionError {

   val messageString = buildString {
      append(clueContextAsString())
      if (message != null) {
         append(message)
      }
      // intellij requires the expected and actual values to be printed in a specific format
      // as the last part of the message string
      if (expected != null && actual != null) {
         append(intellijFormattedComparison(expected, actual))
      }
   }

   return try {
      // in the case of a mock, trying to access the cause's stacktrace in the assertion error constructor
      // will cause another exception to throw
      // easist workaround is to try to access the stack trace ourselves and catch any throwable
      cause?.stackTrace
      if (cause == null)
         AssertionFailedError(messageString, expected?.value?.value, actual?.value?.value)
      else
         AssertionFailedError(messageString, expected?.value?.value, actual?.value?.value, cause)
   } catch (_: Throwable) {
      AssertionFailedError(messageString, expected?.value?.value, actual?.value?.value)
   }
}

/**
 * Returns a message formatted appropriately for intellij to show a diff.
 *
 * This is the format intellij requires in order to recognize the diff:
 * https://github.com/JetBrains/intellij-community/blob/master/java/java-runtime/src/com/intellij/rt/execution/testFrameworks/AbstractExpectedPatterns.java
 *
 * From the above link:
 * private static final Pattern ASSERT_EQUALS_PATTERN = Pattern.compile("expected:<(.*)> but was:<(.*)>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
 *
 * Note - if the printed value contains type information and the types are different, then the type
 * information will be included in the output.
 */
fun intellijFormattedComparison(expected: Expected, actual: Actual): String {

   // only include types if they are different and neither is null
   val includeTypes = when {
      expected.value.type == null || actual.value.type == null -> false
      expected.value.type != actual.value.type -> true
      else -> false
   }

   fun format(printed: Printed): String = when (includeTypes) {
      true if printed.type?.qualifiedName != null -> "${printed.type.qualifiedName}<${printed.value}>"
      else -> "<${printed.value}>"
   }
   return "expected:${format(expected.value)} but was:${format(actual.value)}"
}
