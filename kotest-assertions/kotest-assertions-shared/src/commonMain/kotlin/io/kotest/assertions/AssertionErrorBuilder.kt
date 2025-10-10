package io.kotest.assertions

import io.kotest.assertions.print.Printed
import io.kotest.matchers.errorCollector

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
       * Convenience method to create an [AssertionError] with the given [message] and throw or collect it.
       * The error message provided will have any clue context prepended.
       *
       * Use this method when you want to throw or collect an [AssertionError] with a specific message and have
       * no expected or actual values to compare, or any underlying cause.
       * The collected message will work within `assertSoftly { }` blocks.
       */
      fun failSoftly(message: String) {
         errorCollector.collectOrThrow(
            create().withMessage(message).build()
         )
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
      return createAssertionError(message, cause, expected, actual)
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
   message: String?,
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
