package io.kotest.engine.test

import io.kotest.core.test.Enabled
import io.kotest.engine.test.TestResult
import kotlin.time.Duration

data class TestResultBuilder(
   private val duration: Duration,
   private val error: Throwable?,
   private val ignoreReason: String?,
) {

   companion object {
      fun builder(): TestResultBuilder = TestResultBuilder(Duration.ZERO, null, null)
   }

   fun withDuration(duration: Duration) = copy(duration = duration)
   fun withError(error: Throwable?) = copy(error = error)
   fun withIgnoreReason(ignoreReason: String) = copy(ignoreReason = ignoreReason)
   fun withIgnoreEnabled(enabled: Enabled) = copy(ignoreReason = enabled.reason)
   fun withFailure(message: String) = copy(error = Exception(message))

   fun build(): TestResult {
      return when {
         ignoreReason != null -> TestResult.Ignored(ignoreReason)
         error == null -> TestResult.Success(duration)
         error is AssertionError -> TestResult.Failure(duration, error)
         else -> TestResult.Error(duration, error)
      }
   }

}
