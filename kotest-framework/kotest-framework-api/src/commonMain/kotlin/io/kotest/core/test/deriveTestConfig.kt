package io.kotest.core.test

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import kotlin.time.Duration

/**
 * Creates and returns a new [TestCaseConfig] from the given parameters, using values
 * from the receiver as defaults.
 */
fun TestCaseConfig.deriveTestConfig(
   enabled: Boolean? = null,
   tags: Set<Tag>? = null,
   extensions: List<TestCaseExtension>? = null,
   timeout: Duration? = null,
   invocationTimeout: Duration? = null,
   enabledIf: EnabledIf? = null,
   invocations: Int? = null,
   threads: Int? = null,
   severity: TestCaseSeverityLevel? = null,
   listeners: List<TestListener>? = null,
   enabledOrCause: IsActive? = null,
   enabledOrCauseIf: EnabledOrCauseIf? = null,
) = TestCaseConfig(
   enabled = enabled ?: this.enabled,
   tags = tags ?: this.tags,
   extensions = extensions ?: this.extensions,
   listeners = listeners ?: this.listeners,
   timeout = timeout ?: this.timeout,
   invocationTimeout = invocationTimeout ?: this.invocationTimeout,
   enabledIf = enabledIf ?: this.enabledIf,
   invocations = invocations ?: this.invocations,
   threads = threads ?: this.threads,
   severity = severity ?: this.severity,
   enabledOrCause = enabledOrCause ?: this.enabledOrCause,
   enabledOrCauseIf = enabledOrCauseIf ?: this.enabledOrCauseIf,
)
