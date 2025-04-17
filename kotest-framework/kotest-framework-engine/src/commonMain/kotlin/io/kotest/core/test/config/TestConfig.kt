package io.kotest.core.test.config

import io.kotest.core.Tag
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.RootTest
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCaseSeverityLevel
import kotlin.time.Duration

/**
 * Test config that is attached to a [RootTest] or [NestedTest] during compile time.
 * Values specified here are the ultimate source of truth for configuration.
 *
 * Anything not specified here will bubble up to parent configurations following the runtime
 * resolution rules.
 */
data class TestConfig(

   val enabled: Boolean? = null,
   val enabledIf: EnabledIf? = null,
   val enabledOrReasonIf: EnabledOrReasonIf? = null,

   val invocations: Int? = null,

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

   val extensions: List<Extension>? = null,

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
   val blockingTest: Boolean? = null,

   // if set to > 0, then the test will be retried this many times in the event of a failure
   // if left to null, then the default provided by a spec or the project config will be used
   val retries: Int? = null,

   // if set to to a non null value then this is the delay between retries
   // if left to null, then the default provided by a spec or the project config will be used
   val retryDelay: Duration? = null,
) {
   init {
      require(invocations == null || invocations > 0) { "Number of invocations must be greater than 0" }
      require(timeout?.isPositive() != false) { "Timeout must be positive" }
      require(invocationTimeout?.isPositive() != false) { "Invocation timeout must be positive" }
      require(timeout == null || invocationTimeout == null || invocationTimeout <= timeout) {
         "Invocation timeout must not exceed the test case timeout: " +
            "$invocationTimeout (invocationTimeout) > $timeout (timeout)"
      }
   }

   /**
    * Returns a copy of this [io.kotest.core.test.config.TestConfig] with an enabledIf that
    * returns disabled due to an xmethod override. Calling this method will override
    * any other enabled flags.
    */
   fun withXDisabled(): TestConfig {
      return copy(
         enabled = null,
         enabledIf = null,
         enabledOrReasonIf = { Enabled.disabledByXMethod }
      )
   }
}
