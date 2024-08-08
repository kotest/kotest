package io.kotest.core.test.config

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCaseSeverityLevel
import kotlin.time.Duration

/**
 * Test config that is attached to a [RootTest] or [NestedTest] during compile time.
 *
 * This config is not resolved, and will be converted to a [ResolvedTestConfig] once
 * resolved at runtime.
 */
data class TestConfig(

   val enabled: Boolean? = null,
   val enabledIf: EnabledIf? = null,
   val enabledOrReasonIf: EnabledOrReasonIf? = null,

   val invocations: Int? = null,
   val threads: Int? = null,

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
   val tags: Set<Tag>? = null,

   val extensions: List<TestCaseExtension>? = null,

   val severity: TestCaseSeverityLevel? = null,

   val failfast: Boolean? = null,

   // assertion mode can be set to control errors/warnings in a test
   // if null, defaults will be applied
   val assertionMode: AssertionMode? = null,

   val assertSoftly: Boolean? = null,

   // when set to true, installs a coroutine debug probe for tracing coroutines when an error occurs
   val coroutineDebugProbes: Boolean? = null,

   val coroutineTestScope: Boolean? = null,

   // When set to true, execution will switch to a dedicated thread for each test case in this spec,
   // therefore allowing the test engine to safely interrupt tests via Thread.interrupt when they time out.
   // This is useful if you are testing blocking code and want to use timeouts because coroutine timeouts
   // are cooperative by nature.
   val blockingTest: Boolean? = null
) {
   init {
      require(invocations == null || invocations > 0) { "Number of invocations must be greater than 0" }
      require(threads == null || threads > 0) { "Number of threads must be greater than 0" }
      require((threads ?: 0) <= (invocations ?: 1)) { "Number of threads must be <= number of invocations" }
      require(timeout?.isPositive() ?: true) { "Timeout must be positive" }
      require(invocationTimeout?.isPositive() ?: true) { "Invocation timeout must be positive" }
   }
}
