package io.kotest.core.test

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
data class TestCaseConfig constructor(
   val enabled: Boolean = true,
   @Deprecated("to be replaced with functions")
   val invocations: Int = 1,
   // max time of the test, in millis
   val timeout: Duration? = null,
   // provides for concurrent execution of the test case
   // only has an effect if invocations > 1
   @Deprecated("to be replaced with functions")
   val threads: Int = 1,
   val tags: Set<Tag> = emptySet(),
   val extensions: List<TestCaseExtension> = emptyList(),
   // an issue number, or link to the issue, can be used by plugins
   val issue: String? = null,
   val enabledIf: EnabledIf = { true }
) {
   init {
      require(threads > 0) { "Threads must be > 0" }
      require(invocations > 0) { "Invocations must be > 0" }
   }
}

typealias EnabledIf = () -> Boolean

/**
 * Creates a [TestCaseConfig] from the given parameters, reverting to the
 * receiver for null parameters.
 */
@UseExperimental(ExperimentalTime::class)
fun TestCaseConfig.deriveTestConfig(
   enabled: Boolean? = null,
   tags: Set<Tag>? = null,
   extensions: List<TestCaseExtension>? = null,
   timeout: Duration? = null,
   enabledIf: EnabledIf? = null
) = TestCaseConfig(
   enabled = enabled ?: this.enabled,
   tags = tags ?: this.tags,
   extensions = extensions ?: this.extensions,
   timeout = timeout ?: this.timeout,
   enabledIf = enabledIf ?: this.enabledIf
)

/**
 * Returns the timeout for a [TestCase] taking into account global settings.
 */
@UseExperimental(ExperimentalTime::class)
expect fun TestCaseConfig.resolvedTimeout(): Duration
