package io.kotlintest

actual object Failures {
  
  /**
   * Central point for error creation
   *
   * On the JavaScript platform, an AssertionError is created with the [message], but [cause] is currently ignored,
   * as Kotlin's specification doesn't allow a cause in AssertionError.
   *
   */
  actual fun failure(message: String, cause: Throwable?): AssertionError {
    return AssertionError(message)
  }
  
}