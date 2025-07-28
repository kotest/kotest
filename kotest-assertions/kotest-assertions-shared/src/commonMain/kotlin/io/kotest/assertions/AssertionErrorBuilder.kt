package io.kotest.assertions

import io.kotest.assertions.print.Printed
import io.kotest.matchers.clueContextAsString

/**
 * Use this object to create exceptions on a target platform.
 *
 * This will create the most appropriate exception type, such as org.opentest4j.AssertionFailedError on the JVM
 * or [KotestAssertionFailedError] on other platforms.
 *
 * If the platform supports stack traces, then any provided [cause] stack
 * is cleaned of `io.kotest` lines.
 */
data class AssertionErrorBuilder(
   private val message: String?,
   private val cause: Throwable?,
   private val expected: Expected?,
   private val actual: Actual?,
) {

   companion object {

      /**
       * Convenience method to create an [AssertionError] with the given [message] and throw it.
       * The error message provided will have any clue context prepended.
       *
       * Use this method when you want to throw an [AssertionError] with a specific message and have
       * no expected or actual values to compare, or any underlying cause.
       */
      fun fail(message: String): Nothing {
         throw create().withMessage(message).build()
      }

      /**
       * Creates an [AssertionErrorBuilder].
       */
      fun create(): AssertionErrorBuilder {
         return AssertionErrorBuilder(
            message = null,
            cause = null,
            expected = null,
            actual = null,
         )
      }
   }

   fun withMessage(message: String): AssertionErrorBuilder {
      return copy(message = message)
   }

   fun withValues(expected: Expected, actual: Actual): AssertionErrorBuilder {
      return copy(expected = expected, actual = actual)
   }

   fun withCause(cause: Throwable?): AssertionErrorBuilder {
      return copy(cause = cause)
   }

   fun build(): AssertionError {
      return createAssertionError(generateMessageString(), cause, expected, actual)
   }

   private fun generateMessageString(): String {
      return buildString {
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
}

/**
 * Creates the best error type supported on the platform from the given [message] and [expected] and [actual] values.
 * If the platform supports nested exceptions, the cause is set to the given [cause].
 *
 * If the platform has opentest4j it will use exceptions from that library for compatibility
 * with tools that look for these special exception types to show diffs in the IDE.
 */
expect fun createAssertionError(
   message: String,
   cause: Throwable?,
   expected: Expected?,
   actual: Actual?
): AssertionError

/**
 * Represents the expected value in an assertion error.
 * This is used to to help avoid mixing expected and actual values.
 */
data class Expected(val value: Printed)

/**
 * Represents the actual value in an assertion error.
 * This is used to to help avoid mixing expected and actual values.
 */
data class Actual(val value: Printed)
