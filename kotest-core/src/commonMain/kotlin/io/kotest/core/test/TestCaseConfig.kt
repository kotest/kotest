package io.kotest.core.test

import io.kotest.core.Tag
import io.kotest.core.config.Project
import io.kotest.core.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
data class TestCaseConfig constructor(
   val enabled: Boolean = true,
   val timeout: Duration? = null,
   val tags: Set<Tag> = emptySet(),
   val extensions: List<TestCaseExtension> = emptyList(),
   val enabledIf: EnabledIf = { true }
)

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
fun TestCaseConfig.resolvedTimeout(): Duration = this.timeout ?: Project.timeout()
