package io.kotest.core.test

import kotlin.time.Duration

sealed interface TestResult {

   /**
    * Returns a [TestResult] with a status from the given [reason] string.
    *
    * @param reason an optional string describing why the test was ignored.
    */
   // the test was skipped completely
   data class Ignored(val reason: String?) : TestResult {

      /**
       * Returns a [TestResult.Ignored] with a reason string resolved from the given [Enabled].
       */
      constructor(enabled: Enabled) : this(enabled.reason)

      constructor() : this(null)

      override val duration: Duration = Duration.ZERO
   }

   data class Success(override val duration: Duration) : TestResult

   // the test failed because of some exception that was not an assertion error
   data class Error(override val duration: Duration, val cause: Throwable) : TestResult

   // the test ran but an assertion failed
   data class Failure(override val duration: Duration, val cause: AssertionError) : TestResult

   val duration: Duration

   val name: String
      get() = when (this) {
         is Error -> "Error"
         is Failure -> "Failure"
         is Ignored -> "Ignored"
         is Success -> "Success"
      }

   val reasonOrNull: String?
      get() = when (this) {
         is Ignored -> this.reason
         else -> null
      }

   val errorOrNull: Throwable?
      get() = when (this) {
         is Error -> this.cause
         is Failure -> this.cause
         is Ignored -> null
         is Success -> null
      }

   val isSuccess: Boolean
      get() = this is Success

   val isError: Boolean
      get() = this is Error

   val isFailure: Boolean
      get() = this is Failure

   val isIgnored: Boolean
      get() = this is Ignored

   val isErrorOrFailure: Boolean
      get() = isFailure || isError
}
