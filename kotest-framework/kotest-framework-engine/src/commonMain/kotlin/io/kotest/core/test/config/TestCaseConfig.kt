package io.kotest.core.test.config

import io.kotest.core.Tag
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestCaseSeverityLevel
import kotlin.time.Duration

/**
 * [DefaultTestConfig] is a data class that allows for the configuration of a test case to be easily shared.
 *
 * Each of these values can be overridden on a per-test basis, but if you wish to have a common set of defaults
 * that are shared across several tests, then you can create an instance of this class and declare it in each
 * of the specs that you wish to share the configuration.
 *
 * Inside individual tests, you can override these values by using the DSL functions on the test case.
 *
 */
data class DefaultTestConfig(
   val timeout: Duration? = null,
   val invocationTimeout: Duration? = null,
   val invocations: Int = 1,
   val tags: Set<Tag> = emptySet(),
   val severity: TestCaseSeverityLevel? = null,
   val enabledIf: EnabledIf = { true },
   val enabledOrReasonIf: EnabledOrReasonIf = { Enabled.enabled },
   val assertionMode: AssertionMode? = null,
   val testOrder: TestCaseOrder? = null,
   val blockingTest: Boolean? = null,
   val coroutineTestScope: Boolean? = null,
   val coroutineDebugProbes: Boolean? = null,
   val duplicateTestNameMode: DuplicateTestNameMode? = null,
   val failfast: Boolean? = null,
   var retries: Int? = null,
   var retryFn: ((TestCase) -> Int)? = null,
   var retryDelay: Duration? = null,
   var retryDelayFn: ((TestCase, Int) -> Duration)? = null,
)
