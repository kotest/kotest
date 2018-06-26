package io.kotlintest

object Failures {
  /**
   * Whether KotlinTest-related frames will be removed from the stack traces of thrown [AssertionError]s.
   *
   * This defaults to `true`. You can change it at runtime, or set it with the system property
   * `kotlintest.failures.stacktrace.clean`.
   *
   * e.g.
   *
   * ```
   * -Dkotlintest.failures.stacktrace.clean=false
   * ```
   */
  var shouldRemoveKotlintestElementsFromStacktrace: Boolean = readSystemProperty()

  /**
   * Creates an [AssertionError] with the given [message] and [cause]
   *
   * If [shouldRemoveKotlintestElementsFromStacktrace] is `true`, [removeKotlintestElementsFromStacktrace] will be
   * called on the returned error.
   */
  fun failure(message: String, cause: Throwable?=null): AssertionError = AssertionError(message).apply {
    if (shouldRemoveKotlintestElementsFromStacktrace) {
      removeKotlintestElementsFromStacktrace(this)
    }
    if (cause != null) {
      initCause(cause)
    }
  }

  /**
   * Remove KotlinTest-related elements from the top of [throwable]'s stack trace.
   *
   * If no KotlinTest-related elements are present in the stack trace, it is unchanged.
   */
  fun removeKotlintestElementsFromStacktrace(throwable: Throwable) {
    val stackTrace = throwable.stackTrace
    val lastKotlintestIndex = stackTrace.indexOfLast {
      it.className.startsWith("io.kotlintest") && ! it.className.startsWith("io.kotlintest.runner")
    }
    throwable.stackTrace = stackTrace.drop(lastKotlintestIndex + 1).toTypedArray()
  }

  private fun readSystemProperty(): Boolean {
    return System.getProperty("kotlintest.failures.stacktrace.clean")?.toBoolean() ?: true
  }
}
