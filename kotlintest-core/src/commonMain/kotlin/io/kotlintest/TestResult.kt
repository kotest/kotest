package io.kotlintest

data class TestResult(val status: TestStatus,
                      val error: Throwable?,
                      val reason: String?,
                      val metaData: Map<String, Any?> = emptyMap()) {
  companion object {
    val Success = TestResult(TestStatus.Success, null, null)
    val Ignored = TestResult(TestStatus.Ignored, null, null)
    fun failure(e: AssertionError) = TestResult(TestStatus.Failure, e, null)
    fun error(t: Throwable) = TestResult(TestStatus.Error, t, null)
    fun ignored(reason: String?) = TestResult(TestStatus.Ignored, null, reason)
  }
}