package io.kotest.core.test

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import kotlin.time.Duration

typealias EnabledIf = (TestCase) -> Boolean
typealias EnabledOrReasonIf = (TestCase) -> Enabled

class Enabled private constructor(val isEnabled: Boolean, reason: String? = null) {
   private val builder = StringBuilder(reason ?: "")
   val reason get() = builder.trim().toString()

   companion object {
      val enabled = Enabled(true)
      fun disabled(reason: String?) = Enabled(false, reason)

      fun fold(es: Iterable<Enabled>): Enabled {
         return es.fold(enabled) { acc, e ->
            Enabled(acc.isEnabled && e.isEnabled, acc.reason).also {
               if (!e.isEnabled) {
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
   val enabledOrReasonIf: EnabledOrReasonIf = { Enabled.enabled },
) {
   init {
      require(invocations > 0) { "Number of invocations must be greater than 0" }
      require(threads > 0) { "Number of threads must be greater than 0" }
      require(threads <= invocations) { "Number of threads must be <= number of invocations" }
   }
}

val xdisabledMessage = Enabled.disabled("Test was disabled using xdisabled")

/**
 * Returns a copy of this test config with the enabled flag set to false, if [xdisabled] is true.
 */
fun TestCaseConfig.withXDisabled(xdisabled: Boolean) = if (xdisabled) copy(enabledOrReasonIf = { xdisabledMessage }) else this
