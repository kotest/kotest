package io.kotlintest.matchers

actual object AssertionErrorCollector {
  
  @PublishedApi
  internal actual var shouldCollectErrors: Boolean
    get() = TODO()
    set(value) = TODO()
  
  actual fun collectOrThrow(error: Throwable) {
    TODO()
  }
  
  actual fun throwCollectedErrors() {
    TODO()
  }
  
  actual fun setClueContext(context: String?) {
    TODO()
  }
  
  actual fun getClueContext(): String {
    TODO()
  }
}