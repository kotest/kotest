package io.kotest.engine.test

import io.kotest.core.test.Enabled
import kotlin.time.Duration

data class TestResultBuilder(
   private val duration: Duration,
   private val error: Throwable?,
   private val ignored: Boolean,
   private val ignoreReason: String?,
) {

   companion object {
      fun builder(): TestResultBuilder = TestResultBuilder(Duration.ZERO, null, false, null)
   }

   fun withDuration(duration: Duration) = copy(duration = duration)

   /**
    * Sets a failure that occurred during the test. This is a convenience method that wraps the
    * message in an [Exception] and results in a [TestResult.Error].
    */
   fun withFailure(message: String) = copy(error = Exception(message))

   /**
    * Sets the error that occurred during the test. If this is an [AssertionError] then the built
    * result will be [TestResult.Failure], otherwise it will be [TestResult.Error].
    */
   fun withError(error: Throwable?) = copy(error = error)

   /**
    * Sets the reason for ignoring the test. If this is set then the built result will be
    * [TestResult.Ignored] regardless of any other properties.
    */
   fun withIgnoreReason(ignoreReason: String?) = copy(ignored = true, ignoreReason = ignoreReason)

   /**
    * Sets the reason for ignoring the test. If this is set then the built result will be
    * [TestResult.Ignored] regardless of any other properties.
    */
   fun withIgnoreEnabled(enabled: Enabled) = copy(ignored = true, ignoreReason = enabled.reason)

   fun withIgnore() = copy(ignored = true)

   fun build(): TestResult {
      return when {
         ignored -> TestResult.Ignored(ignoreReason)
         error == null -> TestResult.Success(duration)
         error is AssertionError -> TestResult.Failure(duration, error)
         else -> TestResult.Error(duration, error)
      }
   }

}
