package io.kotest.core

import io.kotest.Tag
import io.kotest.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
data class TestCaseConfig constructor(
  val enabled: Boolean = true,
  val invocations: Int = 1,
  // max time of the test, in millis
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
@UseExperimental(ExperimentalTime::class)
expect fun TestCaseConfig.resolvedTimeout(): Duration
