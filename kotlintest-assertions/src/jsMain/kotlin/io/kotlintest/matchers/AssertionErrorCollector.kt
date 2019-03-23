package io.kotlintest.matchers

actual object AssertionErrorCollector {

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