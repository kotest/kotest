package io.kotlintest

import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

data class TestCaseConfig(
    val enabled: Boolean = true,
    val invocations: Int = 1,
    val timeout: Duration = Duration.ofSeconds(600),
    // provides for concurrent execution of the test case
    // only has an effect if invocations > 1
    val threads: Int = 1,
    val tags: Set<Tag> = emptySet(),
    val extensions: List<TestCaseExtension> = emptyList()) {
  init {
    require(threads > 0) { "Parallelism must be > 0" }
    require(invocations > 0) { "Invocations must be > 0" }
  }
}