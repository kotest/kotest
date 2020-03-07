package io.kotest.core.test

import io.kotest.core.Tag
import io.kotest.core.config.Project
import io.kotest.core.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class TestCaseConfig constructor(
   val enabled: Boolean = true,
   val invocations: Int = 1,
   val threads: Int = 1,
   val timeout: Duration? = null,
   val tags: Set<Tag> = emptySet(),
   val extensions: List<TestCaseExtension> = emptyList(),
   val enabledIf: EnabledIf = { true }
) {
   init {
      require(invocations > 0) { "Number of invocations must be greater than 0" }
      require(threads > 0) { "Number of threads must be greater than 0" }
      require(threads <= invocations) { "Number of threads must be <= number of invocations" }
   }
}

typealias EnabledIf = (TestCase) -> Boolean

/**
 * Creates a [TestCaseConfig] from the given parameters, reverting to the
 * receiver for null parameters.
 */
@OptIn(ExperimentalTime::class)
fun TestCaseConfig.deriveTestConfig(
   enabled: Boolean? = null,
   tags: Set<Tag>? = null,
   extensions: List<TestCaseExtension>? = null,
   timeout: Duration? = null,
   enabledIf: EnabledIf? = null,
   invocations: Int? = null,
   threads: Int? = null
) = TestCaseConfig(
   enabled = enabled ?: this.enabled,
   tags = tags ?: this.tags,
   extensions = extensions ?: this.extensions,
   timeout = timeout ?: this.timeout,
   enabledIf = enabledIf ?: this.enabledIf,
   invocations = invocations ?: this.invocations,
   threads = threads ?: this.threads
)

/**
 * Returns the timeout for a [TestCase] taking into account global settings.
 */
@OptIn(ExperimentalTime::class)
fun TestCaseConfig.resolvedTimeout(): Duration = this.timeout ?: Project.timeout()
