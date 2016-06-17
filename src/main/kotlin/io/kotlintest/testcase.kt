package io.kotlintest

import java.util.concurrent.TimeUnit

data class TestSuite(val name: String, val nestedSuites: MutableList<TestSuite>, val cases: MutableList<io.kotlintest.TestCase>) {
  companion object {
    fun empty(name: String) = TestSuite(name, mutableListOf<TestSuite>(), mutableListOf<io.kotlintest.TestCase>())
  }
}

data class TestConfig(var ignored: Boolean = false,
                      var invocations: Int = 1,
                      var timeout: Duration = Duration.unlimited,
                      var threads: Int = 1,
                      var tags: List<String> = listOf()) {
  constructor(
          ignored: Boolean,
          invocations: Int,
          timeout: Long = 0,
          timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
          threads: Int,
          tags: List<String>): this(ignored, invocations, Duration(timeout, timeoutUnit), threads, tags)
}

data class TestCase(val suite: TestSuite,
                    val name: String,
                    val test: () -> Unit,
                    val config: TestConfig = TestConfig()) {

  fun config(invocations: Int = 1,
             ignored: Boolean = false,
             timeout: Duration = Duration.unlimited,
             threads: Int = 1,
             tag: String? = null,
             tags: List<String> = listOf()): Unit {
    config.invocations = invocations
    config.ignored = ignored
    config.timeout = timeout
    config.threads = threads
    config.tags = tags
    if (tag != null)
      config.tags = config.tags.plus(tag)
  }

  @Deprecated(
          message = "use overload instead",
          replaceWith = ReplaceWith("config(invocations, ignored, timeout, threads, tag, tags)"))
  fun config(invocations: Int = 1,
             ignored: Boolean = false,
             timeout: Long = 0,
             timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
             threads: Int = 1,
             tag: String? = null,
             tags: List<String> = listOf()): Unit {
    config.invocations = invocations
    config.ignored = ignored
    config.timeout = Duration(timeout, timeoutUnit)
    config.threads = threads
    config.tags = tags
    if (tag != null)
      config.tags = config.tags.plus(tag)
  }

  fun active(): Boolean = !config.ignored
}