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
    val error = AssertionError(message)
    return modifyThrowable(error)
  }
  
  /**
   * Returns [throwable] after KotlinTest modifications
   *
   * This method, along with [failure] centralizes the creation of Assertion Errors on all platforms.
   * The errors might be tempered with depending on the platform and its features. These changes may include StackTrace
   * manipulation, for example.
   *
   */
  actual fun <T : Throwable> modifyThrowable(throwable: T): T {
    return throwable
  }
  
}