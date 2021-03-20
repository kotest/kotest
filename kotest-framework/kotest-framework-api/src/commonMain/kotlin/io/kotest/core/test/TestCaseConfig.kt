package io.kotest.core.test

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import kotlin.time.Duration

typealias EnabledIf = (TestCase) -> Boolean
typealias EnabledOrCauseIf = (TestCase) -> IsActive

class IsActive private constructor(val active: Boolean, reason: String) {
   private val builder = StringBuilder(reason)
   val reason get() = builder.trim().toString()

   companion object {
      val active = IsActive(true, "")
      fun inactive(reason: String) = IsActive(false, reason)

      fun fold(isActives: Iterable<IsActive>): IsActive {
         return isActives.fold(active) { acc, e ->
            IsActive(acc.active && e.active, acc.reason).also {
               if (!e.active) {
                  it.builder.appendLine()
                  it.builder.append(e.reason)
               }
            }
         }
      }
   }
}

data class TestCaseConfig(
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
   val tags: Set<Tag> = emptySet(),
   val listeners: List<TestListener> = emptyList(),
   val extensions: List<TestCaseExtension> = emptyList(),
   val enabledIf: EnabledIf = { true },
   val severity: TestCaseSeverityLevel? = null,
   val enabledOrCause: IsActive = IsActive.active,
   val enabledOrCauseIf: EnabledOrCauseIf = { IsActive.active },
) {
   init {
      require(invocations > 0) { "Number of invocations must be greater than 0" }
      require(threads > 0) { "Number of threads must be greater than 0" }
      require(threads <= invocations) { "Number of threads must be <= number of invocations" }
   }
}

const val xdisabledMessage = "Test was disabled using xdisabled"

/**
 * Returns a copy of this test config with the enabled flag set to false, if [xdisabled] is true.
 */
fun TestCaseConfig.withXDisabled(xdisabled: Boolean) = if (xdisabled) copy(enabledOrCause = IsActive.inactive(xdisabledMessage)) else this
