package io.kotest.engine.test

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseConfig
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

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
   invocationTimeout: Duration? = null,
   enabledIf: EnabledIf? = null,
   invocations: Int? = null,
   threads: Int? = null,
   listeners: List<TestListener>? = null
) = TestCaseConfig(
    enabled = enabled ?: this.enabled,
    tags = tags ?: this.tags,
    extensions = extensions ?: this.extensions,
    listeners = listeners ?: this.listeners,
    timeout = timeout ?: this.timeout,
    invocationTimeout = invocationTimeout ?: this.invocationTimeout,
    enabledIf = enabledIf ?: this.enabledIf,
    invocations = invocations ?: this.invocations,
    threads = threads ?: this.threads
)
