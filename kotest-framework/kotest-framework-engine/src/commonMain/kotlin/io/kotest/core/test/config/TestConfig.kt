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

   /**
    * When set to true, this test, and any nested tests, will be executed inside a runTest
    * block from the `kotlin.test` library.
    *
    * Any test executing in such a `runTest` block will use virtual time via a [kotlinx.coroutines.test.TestDispatcher].
    *
    * The scheduler is the central source of truth for virtual time in a test. It performs two critical roles:
    * - Skips Delays: It automatically fast-forwards through delay() calls.
    * - Orchestrates Execution: It maintains a queue of tasks and determines their execution order based on their scheduled virtual time.
    *
    * Task Queuing: When you launch a coroutine on a TestDispatcher, the dispatcher doesn't run the code itself.
    * Instead, it sends the task to its linked scheduler.
    *
    * Clock Management: The scheduler waits for you to manually move the clock. When you call methods
    * like `advanceUntilIdle` or `advanceTimeBy(ms)` on the scheduler, it tells the linked dispatchers to execute the
    * tasks whose time has come.
    *
    * Synchronization: You can link multiple TestDispatchers to the same scheduler. This ensures that even
    * if different parts of your code use different dispatchers (e.g., one for Main, one for IO), they all share
    * the same virtual clock and stay in sync.
    *
    * You typically interact with the scheduler through a `testScheduler` variable - exposed as an extension value
    * inside the test scope - which exposes these controls:
    *
    * `testScheduler.runCurrent()`: Runs tasks scheduled at the current virtual time.
    * `testScheduler.advanceTimeBy(delay)`: Moves the clock forward and executes tasks in that window.
    * `testScheduler.advanceUntilIdle()`: Fast-forwards until there are no more tasks left to run.
    *
    * Note that [timeout] or [invocationTimeout] settings on a test are always treated as real time (or wall clock).
    * That is, if a test has a timeout of say 10 seconds, then invoking `delay(2.hours)` when [coroutineTestScope]
    * is true would not cause the test to time out, because the delay is skipped instantly, and has not taken
    * 10 seconds of real time.
    */
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
    * Returns a copy of this [TestConfig] with [enabledOrReasonIf] set to return [Enabled.disabledByXMethod].
    * Calling this method will override any other enabled flags.
    */
   fun withXDisabled(): TestConfig {
      return copy(
         enabled = null,
         enabledIf = null,
         enabledOrReasonIf = { Enabled.disabledByXMethod }
      )
   }
}
