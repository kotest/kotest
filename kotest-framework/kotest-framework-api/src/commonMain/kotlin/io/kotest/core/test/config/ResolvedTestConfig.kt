package io.kotest.core.test.config

import io.kotest.core.Tag
import io.kotest.core.config.Defaults
import io.kotest.core.extensions.Extension
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCaseSeverityLevel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Runtime resolved config attached to a [io.kotest.core.test.TestCase].
 *
 * Settings specified here have been resolved against spec level and project level defaults
 * and are the final definitive settings to be used by a test case.
 */
data class ResolvedTestConfig(

   val enabled: EnabledOrReasonIf,

   val invocations: Int,

   val threads: Int,

   /**
    * The timeout for a test case and all it's invocations. For example, if this value was set to 800ms,
    * and invocations was 1 (which is the default and typical value), then that single invocation has
    * the full 800ms to complete. But if invocations was 2 for example, then the 800ms would apply to
    * the total time and both invocations would need to complete collectively in 800ms.
    *
    * To set a timeout per invocation see [invocationTimeout].
    */
   val timeout: Duration,

   /**
    * This timeout applies to individual invocations of a test case. If invocations is 1, then this
    * has the same effect as timeout. To set a timeout across all invocations then see [timeout].
    */
   val invocationTimeout: Duration,

   /**
    * [Tag]s that are applied to this test case, in addition to any tags declared on
    * the containing .
    */
   val tags: Set<Tag>,

   /**
    * Any [Extension]s applicable only to this test case.
    */
   val extensions: List<Extension>,

   val severity: TestCaseSeverityLevel,

   val failfast: Boolean,

   /**
    *  Assertion mode can be set to control errors/warnings in a test.
    */
   val assertionMode: AssertionMode,

   /**
    * Whether soft assertion mode should be applied for the entire test.
    */
   val assertSoftly: Boolean,

   /**
    * When set to true, a coroutine debug probe is installed for tracing coroutines when an error occurs.
    */
   val coroutineDebugProbes: Boolean,

   val coroutineTestScope: Boolean,

   // When set to true, execution will switch to a dedicated thread for each test case in this spec,
   // therefore allowing the test engine to safely interrupt tests via Thread.interrupt when they time out.
   // This is useful if you are testing blocking code and want to use timeouts because coroutine timeouts
   // are cooperative by nature.
   val blockingTest: Boolean,
) {
   init {
      require(invocations > 0) { "Number of invocations must be greater than 0" }
      require(threads > 0) { "Number of threads must be greater than 0" }
   }

   companion object {
      val default = ResolvedTestConfig(
         { Enabled.enabled },
         invocations = 1,
         threads = 1,
         coroutineDebugProbes = false,
         coroutineTestScope = false,
         assertionMode = AssertionMode.None,
         assertSoftly = false,
         blockingTest = false,
         extensions = emptyList(),
         timeout = Defaults.defaultTimeoutMillis.milliseconds,
         invocationTimeout = Defaults.defaultInvocationTimeoutMillis.milliseconds,
         tags = emptySet(),
         severity = TestCaseSeverityLevel.TRIVIAL,
         failfast = false,
      )
   }
}
