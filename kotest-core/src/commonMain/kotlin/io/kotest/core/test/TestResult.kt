package io.kotest.core.test

import io.kotest.mpp.bestName

data class TestResult(
   val status: TestStatus,
   val error: Throwable?,
   val reason: String?,
   val duration: Long
) {
   companion object {
      fun success(duration: Long) = TestResult(
         TestStatus.Success,
         null,
         null,
         duration
      )

      /**
       * Returns a [TestResult] derived from a throwable.
       * If the throwable is either an [AssertionError] or one of the library specific assertion types,
       * then a [TestStatus.Failure] will be returned, otherwise a [TestStatus.Error] will be returned.
       */
      fun throwable(e: Throwable, duration: Long): TestResult {
         return when (e) {
            is AssertionError -> failure(e, duration)
            else -> when (e::class.bestName()) {
               "org.opentest4j.AssertionFailedError", "AssertionFailedError" -> failure(e, duration)
               "org.junit.ComparisonFailure", "ComparisonFailure" -> failure(e, duration)
               else -> error(e, duration)
            }
         }
      }

      val Ignored = TestResult(
         TestStatus.Ignored,
         null,
         null,
         0
      )

      private fun failure(e: Throwable, duration: Long) = TestResult(
         TestStatus.Failure,
         e,
         null,
         duration
      )

      private fun error(t: Throwable, duration: Long) = TestResult(
         TestStatus.Error,
         t,
         null,
         duration
      )

      fun ignored(reason: String?) = TestResult(
         TestStatus.Ignored,
         null,
         reason,
         0
      )
   }
}

enum class TestStatus {
   // the test was skipped completely
   Ignored,

   // the test was successful
   Success,

   // the test failed because of some exception that was not an assertion error
   Error,

   // the test ran but an assertion failed
   Failure
}
