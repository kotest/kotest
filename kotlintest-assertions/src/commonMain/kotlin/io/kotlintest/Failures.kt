package io.kotlintest



expect object Failures {
  
  /**
   * Central point for error creation
   *
   * This method is centralizes the creation of Assertion Errors on all platforms.
   * The errors might be tempered with depending on the platform and its features. These changes may include StackTrace
   * manipulation, for example.
   *
   */
  fun failure(message: String, cause: Throwable? = null): AssertionError
}