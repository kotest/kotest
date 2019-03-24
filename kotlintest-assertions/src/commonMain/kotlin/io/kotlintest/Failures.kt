package io.kotlintest

/**
 * Verifies that [block] throws an [AssertionError]
 *
 * If [block] throws an [AssertionError], this method will pass. Otherwise, it will throw an error, as a failure was
 * expected.
 *
 * This should be used mainly to check that an assertion fails, for example:
 *
 * ```
 *     shouldFail {
 *        1 shouldBe 2  // This should fail
 *     }
 * ```
 *
 * @see shouldThrowAny
 * @see shouldThrow
 * @see shouldThrowExactly
 */
fun shouldFail(block: () -> Any?): AssertionError = shouldThrow(block)


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