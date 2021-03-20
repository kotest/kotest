package io.kotest.core.test

data class TestResult(
   val status: TestStatus,
   val error: Throwable?,
   val reason: String?, // the reason the test was ignored
   val duration: Long
) {
   companion object {

      /**
       * Creates a new [TestResult] with status [TestStatus.Success].
       * @param duration the execution time of the test case.
       */
      fun success(duration: Long) = TestResult(
         TestStatus.Success,
         null,
         null,
         duration
      )

      /**
       * Returns a [TestResult] with status [TestStatus.Ignored].
       */
      val Ignored = TestResult(
         TestStatus.Ignored,
         null,
         null,
         0
      )

      /**
       * Creates a new [TestResult] with status [TestStatus.Failure].
       *
       * @param e the assertion error that caused the test case to fail.
       * @param duration the execution time of the test case.
       */
      fun failure(e: AssertionError, duration: Long) = TestResult(
         TestStatus.Failure,
         e,
         null,
         duration
      )

      /**
       * Creates a new [TestResult] with status [TestStatus.Error].
       *
       * @param t the general thorwable that caused the test case to error.
       * @param duration the execution time of the test case.
       */
      fun error(t: Throwable, duration: Long) = TestResult(
         TestStatus.Error,
         t,
         null,
         duration
      )

      /**
       * Returns a [TestResult] with status [TestStatus.Ignored] and a reason string resolved from [TestCase.isActive].
       *
       * @param isActive a inactive [IsActive] that contains the reason for the test being ignored.
       */
      fun ignored(isActive: IsActive): TestResult {
         require(!isActive.active) { "An ignored test must not be active" }
         return ignored(isActive.reason)
      }

      /**
       * Returns a [TestResult] with status [TestStatus.Ignored] and a custom reason string.
       *
       * @param reason an optional string describing why the test was ignored.
       */
      fun ignored(reason: String?) = TestResult(
         TestStatus.Ignored,
         null,
         reason,
         0
      )
   }
}
