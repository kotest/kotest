package io.kotest.core.test.config

import io.kotest.core.Tag
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCaseSeverityLevel
import kotlin.time.Duration

data class DefaultTestConfig(
   val timeout: Duration? = null,
   val invocationTimeout: Duration? = null,
   val threads: Int = 1,
   val invocations: Int = 1,
   val tags: Set<Tag> = emptySet(),
   val severity: TestCaseSeverityLevel? = null,
   val enabledIf: EnabledIf = { true },
   val enabledOrReasonIf: EnabledOrReasonIf = { Enabled.enabled },
   val assertionMode: AssertionMode? = null,
)
