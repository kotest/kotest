package io.kotlintest

data class TestResult(val status: TestStatus,
                      val error: Throwable?,
                      val reason: String?,
                      val durationMs: Long,
                      val metaData: Map<String, Any?> = emptyMap()) {
  companion object {
    fun success(durationMs: Long) = TestResult(TestStatus.Success, null, null, durationMs)
    val Ignored = TestResult(TestStatus.Ignored, null, null, 0)
    fun failure(e: AssertionError, durationMs: Long) = TestResult(TestStatus.Failure, e, null, durationMs)
    fun error(t: Throwable, durationMs: Long) = TestResult(TestStatus.Error, t, null, durationMs)
    fun ignored(reason: String?) = TestResult(TestStatus.Ignored, null, reason, 0)
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
