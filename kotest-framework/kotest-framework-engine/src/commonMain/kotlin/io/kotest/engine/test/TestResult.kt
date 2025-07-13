package io.kotest.engine.test

sealed interface TestResult {

   /**
    * Creates an ignored [TestResult] with a status from the given [reason] string.
    *
    * @param reason an optional string describing why the test was ignored.
    */
   data class Ignored(val reason: String?) : TestResult {
      constructor() : this(null)
      override val duration: kotlin.time.Duration = _root_ide_package_.kotlin.time.Duration.Companion.ZERO
   }

   data class Success(override val duration: kotlin.time.Duration) : TestResult

   // the test failed because of some exception that was not an assertion error
   data class Error(override val duration: kotlin.time.Duration, val cause: Throwable) : TestResult

   // the test ran but an assertion failed
   data class Failure(override val duration: kotlin.time.Duration, val cause: AssertionError) : TestResult

   val duration: kotlin.time.Duration

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
