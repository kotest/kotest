package io.kotest.assertions

import io.kotest.common.errors.ComparisonError

expect val exceptions: Exceptions

/**
 * Use this object to create exceptions on a target platform.
 * This will create the most appropriate exception type, such as org.opentest4j.AssertionFailedError on
 * platforms that support it, and defaulting to the basic kotlin AssertionError in the degenerative case.
 */
interface Exceptions {

   /**
    * Creates an [AssertionError] from the given message. If the platform supports nested exceptions, the cause
    * is set to the given [cause].
    */
   fun createAssertionError(message: String, cause: Throwable?): AssertionError

   /**
    * Creates the best error type supported on the platform (eg opentest4j.AssertionFailedException) from the
    * given message and expected and actual values. If the platform supports nested exceptions, the cause
    * is set to the given [cause].
    *
    * If the platform has opentest4j it will use exceptions from that library
    * for compatibility with tools that look for these special exception types.
    */
   fun createAssertionError(message: String, cause: Throwable?, expected: Expected, actual: Actual): Throwable
}

class AssertionFailedError(
   message: String,
   override val expectedValue: String,
   override val actualValue: String
) : AssertionError(message), ComparisonError
