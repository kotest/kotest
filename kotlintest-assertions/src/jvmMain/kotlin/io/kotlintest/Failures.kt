package io.kotlintest

actual object Failures {

  /**
   * Whether KotlinTest-related frames will be removed from the stack traces of thrown [AssertionError]s.
   *
   * This defaults to `true`. You can change it by setting the system property `kotlintest.failures.stacktrace.clean`
   * or at runtime, by reassigning this var.
   *
   * E.g.:
   *
   * ```
   *     -Dkotlintest.failures.stacktrace.clean=false
   * ```
   *
   * or
   *
   * ```
   *     Failures.shouldRemoveKotlintestElementsFromStacktrace = false
   * ```
   */
  var shouldRemoveKotlintestElementsFromStacktrace = readSystemProperty()
  
  private fun readSystemProperty(): Boolean {
    return System.getProperty("kotlintest.failures.stacktrace.clean")?.toBoolean() ?: true
  }

  /**
   * Creates an [AssertionError] with the given [message] and [cause]
   *
   * If [shouldRemoveKotlintestElementsFromStacktrace] is `true`, the stacktrace will be reduced to the user-code
   * StackTrace only.
   */
  actual fun failure(message: String, cause: Throwable?): AssertionError = AssertionError(message).apply {
    if (shouldRemoveKotlintestElementsFromStacktrace) {
      removeKotlintestElementsFromStacktrace(this)
    }
    initCause(cause)
  }

  /**
   * Remove KotlinTest-related elements from the top of [throwable]'s stack trace.
   *
   * If no KotlinTest-related elements are present in the stack trace, it is unchanged.
   */
  fun removeKotlintestElementsFromStacktrace(throwable: Throwable) {
    throwable.stackTrace = UserStackTraceConverter.getUserStacktrace(throwable.stackTrace)
  }
  
}

private object UserStackTraceConverter {
  
  fun getUserStacktrace(kotlintestStacktraces: Array<StackTraceElement>): Array<StackTraceElement> {
    return kotlintestStacktraces.dropUntilUserClass()
  }
  
  /**
   * Drops stacktraces until it finds a Kotlintest Stacktrace then drops stacktraces until it finds a non-Kotlintest stacktrace
   *
   * Sometimes, it's possible for the Stacktrace to contain classes that are not from Kotlintest,
   * such as classes from sun.reflect or anything from Java. After clearing these classes, we'll be at Kotlintest
   * stacktrace, which will contain exceptions from the Runners and some other classes
   * After everything from Kotlintest we'll finally be at user classes, at which point the stacktrace is clean and is
   * returned.
   */
  private fun Array<StackTraceElement>.dropUntilUserClass(): Array<StackTraceElement> {
    return toList().dropUntilFirstKotlintestClass().dropUntilFirstNonKotlintestClass().toTypedArray()
  }
  
  private fun List<StackTraceElement>.dropUntilFirstKotlintestClass(): List<StackTraceElement> {
    return dropWhile {
      it.isNotKotlintestClass()
    }
  }
  
  private fun List<StackTraceElement>.dropUntilFirstNonKotlintestClass(): List<StackTraceElement> {
    return dropWhile {
      it.isKotlintestClass()
    }
  }
  
  private fun StackTraceElement.isKotlintestClass(): Boolean {
    return className.startsWith("io.kotlintest")
  }
  
  private fun StackTraceElement.isNotKotlintestClass(): Boolean {
    return !isKotlintestClass()
  }
  
}
