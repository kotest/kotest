package io.kotest.common.stacktrace

object UserStackTraceConverter {

  fun getUserStacktrace(kotestStacktraces: Array<StackTraceElement>): Array<StackTraceElement> {
    return kotestStacktraces.dropUntilUserClass()
  }

  /**
   * Drops stacktraces until it finds a Kotest Stacktrace then drops stacktraces until it finds a non-Kotest stacktrace
   *
   * Sometimes, it's possible for the Stacktrace to contain classes that are not from Kotest,
   * such as classes from sun.reflect or anything from Java. After clearing these classes, we'll be at Kotest
   * stacktrace, which will contain exceptions from the Runners and some other classes
   * After everything from Kotest we'll finally be at user classes, at which point the stacktrace is clean and is
   * returned.
   */
  private fun Array<StackTraceElement>.dropUntilUserClass(): Array<StackTraceElement> {
    return toList().dropUntilFirstKotestClass().dropUntilFirstNonKotestClass().toTypedArray()
  }

  private fun List<StackTraceElement>.dropUntilFirstKotestClass(): List<StackTraceElement> {
    return dropWhile {
      it.isNotKotestClass()
    }
  }

  private fun List<StackTraceElement>.dropUntilFirstNonKotestClass(): List<StackTraceElement> {
    return dropWhile {
      it.isKotestClass()
    }
  }

  private fun StackTraceElement.isKotestClass(): Boolean {
    return className.startsWith("io.kotest")
  }

  private fun StackTraceElement.isNotKotestClass(): Boolean {
    return !isKotestClass()
  }

}
