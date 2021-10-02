package io.kotest.core.test

import io.kotest.common.ExperimentalKotest
import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import kotlin.time.Duration

/**
 * Creates and returns a new [TestCaseConfig] from the given parameters, using values
 * from the receiver as defaults.
 */
internal fun TestCaseConfig.deriveTestCaseConfig(
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
   enabledOrReasonIf: EnabledOrReasonIf? = null,
   coroutineDebugProbes: Boolean? = null,
   blockingTest: Boolean? = null,
   testCoroutineDispatcher: Boolean? = null,
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
   enabledOrReasonIf = enabledOrReasonIf ?: this.enabledOrReasonIf,
   failfast = failfast,
   coroutineDebugProbes = coroutineDebugProbes,
   blockingTest = blockingTest,
   testCoroutineDispatcher = testCoroutineDispatcher,
)

/**
 * Creates and returns a new [TestContainerConfig] from the given parameters, using values
 * from the receiver as defaults.
 */
@ExperimentalKotest
internal fun TestContainerConfig.deriveTestContainerConfig(
   enabled: Boolean? = null,
   enabledIf: EnabledIf? = null,
   enabledOrReasonIf: EnabledOrReasonIf? = null,
   tags: Set<Tag>? = null,
   timeout: Duration? = null,
   failfast: Boolean? = null,
) = TestContainerConfig(
   tags = tags ?: this.tags,
   timeout = timeout ?: this.timeout,
   enabled = enabled ?: this.enabled,
   enabledIf = enabledIf ?: this.enabledIf,
   enabledOrReasonIf = enabledOrReasonIf ?: this.enabledOrReasonIf,
   failfast = failfast,
)
