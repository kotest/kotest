package io.kotest.assertions

/**
 * A multiplatform equivalent of opentest4j's AssertionFailedError.
 *
 * This is defined in the shared module so that it can be used by the Kotest engine when
 * generating TeamCity service messages, which are used to report assertion diffs.
 */
data class KotestAssertionFailedError(
   override val message: String,
   override val cause: Throwable?,
   val expected: String?,
   val actual: String?,
) : AssertionError(message)
