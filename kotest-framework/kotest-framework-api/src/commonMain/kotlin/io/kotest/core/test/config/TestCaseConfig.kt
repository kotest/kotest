package io.kotest.core.test.config

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCaseSeverityLevel
import kotlin.time.Duration

@Deprecated("Deprecated in favor of ResolvedTestConfig")
data class TestCaseConfig(

   /**
    * If set to false, this test and any nested tests will be disabled.
    */
   val enabled: Boolean = true,
   val invocations: Int = 1,
   val threads: Int = 1,

   /**
    * The timeout for a test case and all it's invocations. For example, if this value was set to 800ms,
    * and invocations was 1 (which is the default and typical value), then that single invocation has
    * the full 800ms to complete. But if invocations was 2 for example, then the 800ms would apply to
    * the total time across both those invocations.
    *
    * To set a timeout per invocation see [invocationTimeout].
    */
   val timeout: Duration? = null,

   /**
    * This timeout applies to individual invocations of a test case. If invocations is 1, then this
    * has the same effect as timeout. To set a timeout across all invocations then see [timeout].
    */
   val invocationTimeout: Duration? = null,

   /**
    * [Tag]s that are applied to this test case, in addition to any tags declared on
    * the containing spec or parent tests.
    */
   val tags: Set<Tag> = emptySet(),

   val listeners: List<TestListener> = emptyList(),
   val extensions: List<TestCaseExtension> = emptyList(),

   /**
    * If this function evaluates to false, then this test and any nested tests will be disabled.
    */
   val enabledIf: EnabledIf = { true },
   val severity: TestCaseSeverityLevel? = null,
   val enabledOrReasonIf: EnabledOrReasonIf = { Enabled.enabled },

   // assertion mode can be set to control errors/warnings in a test
   // if null, defaults will be applied
   val assertionMode: AssertionMode? = null,
) {
   init {
      require(invocations > 0) { "Number of invocations must be greater than 0" }
      require(threads > 0) { "Number of threads must be greater than 0" }
   }
}
