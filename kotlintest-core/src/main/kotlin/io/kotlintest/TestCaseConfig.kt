package io.kotlintest

import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

data class TestCaseConfig(
  val enabled: Boolean = true,
  val invocations: Int = 1,
  val timeout: Duration? = null,
    // provides for concurrent execution of the test case
    // only has an effect if invocations > 1
  val threads: Int = 1,
  val tags: Set<Tag> = emptySet(),
  val extensions: List<TestCaseExtension> = emptyList(),
    // an issue number, or link to the issue, can be used by plugins
  val issue: String? = null) {
  init {
    require(threads > 0) { "Threads must be > 0" }
    require(invocations > 0) { "Invocations must be > 0" }
  }
}

/**
 * Returns the timeout for a [TestCase] taking into account global settings.
 */
fun TestCaseConfig.timeout(): Duration = this.timeout ?: Project.timeout()
