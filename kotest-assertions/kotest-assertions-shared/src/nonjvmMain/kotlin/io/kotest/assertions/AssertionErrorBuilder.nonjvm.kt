package io.kotest.assertions

import io.kotest.matchers.clueContextAsString

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

   return KotestAssertionFailedError(
      message = messageString,
      cause = cause,
      expected = expected?.value?.value,
      actual = actual?.value?.value
   )
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
   return "expected:<${expected.value.value}> but was:<${actual.value.value}>"
}
