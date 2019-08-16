package io.kotlintest.assertions

/**
 * Handles creation of errors and cleaning of stack traces.
 */
expect object Failures {

  /**
   * Creates an [AssertionError] with the given [message] and [cause]
   *
   */
  fun failure(message: String): AssertionError

  fun failure(message: String, cause: Throwable?): AssertionError

  /**
   * JVM only: If [shouldRemoveKotlintestElementsFromStacktrace] is `true`,
   * the stacktrace will be reduced to the user-code StackTrace only.
   */
  fun clean(throwable: Throwable): Throwable
}
