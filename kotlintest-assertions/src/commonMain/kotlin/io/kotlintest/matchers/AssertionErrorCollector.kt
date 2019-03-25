package io.kotlintest.matchers

expect object AssertionErrorCollector {
  @PublishedApi internal var shouldCollectErrors: Boolean
  fun collectOrThrow(error: Throwable)
  fun throwCollectedErrors()
  fun setClueContext(context: String?)
  fun getClueContext(): String
}