package io.kotlintest.matchers

expect object AssertionErrorCollector {
  fun collectOrThrow(error: Throwable)
  fun throwCollectedErrors()
  fun setClueContext(context: String?)
  fun getClueContext(): String
}