package io.kotest.core.test

import io.kotest.assertions.bestName
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
data class TestResult(
   val status: TestStatus,
   val error: Throwable?,
   val reason: String?,
   val duration: Duration
) {
   companion object {
      fun success(duration: Duration) = TestResult(
         TestStatus.Success,
         null,
         null,
         duration
      )

      fun throwable(e: Throwable?, duration: Duration): TestResult = when (e) {
         null -> success(duration)
         is AssertionError -> failure(e, duration)
         else -> when (e::class.bestName()) {
            "org.opentest4j.AssertionFailedError" -> failure(e, duration)
            else -> error(e, duration)
         }
      }

      val Ignored = TestResult(
         TestStatus.Ignored,
         null,
         null,
         Duration.ZERO
      )

      fun failure(e: Throwable, duration: Duration) = TestResult(
         TestStatus.Failure,
         e,
         null,
         duration
      )

      fun error(t: Throwable, duration: Duration) = TestResult(
         TestStatus.Error,
         t,
         null,
         duration
      )

      fun ignored(reason: String?) = TestResult(
         TestStatus.Ignored,
         null,
         reason,
         Duration.ZERO
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
