package io.kotlintest.core

import io.kotlintest.Tag
import io.kotlintest.extensions.TestCaseExtension

data class TestCaseConfig(
  val enabled: Boolean = true,
  val invocations: Int = 1,
  // max time of the test, in millis
  val timeout: Long? = null,
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
expect fun TestCaseConfig.resolvedTimeout(): Long
