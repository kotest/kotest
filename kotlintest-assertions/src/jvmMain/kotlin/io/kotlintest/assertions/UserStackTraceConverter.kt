package io.kotlintest.assertions

object UserStackTraceConverter {

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
