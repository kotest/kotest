package io.kotlintest

import java.util.concurrent.TimeUnit

data class TestSuite(val name: String, val nestedSuites: MutableList<TestSuite>, val cases: MutableList<io.kotlintest.TestCase>) {
  companion object {
    fun empty(name: String) = TestSuite(name, mutableListOf<TestSuite>(), mutableListOf<io.kotlintest.TestCase>())
  }
}

data class TestCase(val suite: TestSuite,
                    val name: String,
                    val test: () -> Unit,
                    var ignored: Boolean = false,
                    var invocations: Int = 1,
                    var timeout: Long = 0,
                    var timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
                    var threads: Int = 1,
                    var tags: List<String> = listOf()) {

  fun config(invocations: Int = 1,
             ignored: Boolean = false,
             timeout: Long = 0,
             timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
             threads: Int = 1,
             tag: String? = null,
             tags: List<String> = listOf()): Unit {
    this.invocations = invocations
    this.ignored = ignored
    this.timeout = timeout
    this.timeoutUnit = timeoutUnit
    this.threads = threads
    this.tags = tags
    if (tag != null)
      this.tags = this.tags.plus(tag)
  }

  fun active(): Boolean = !ignored
}