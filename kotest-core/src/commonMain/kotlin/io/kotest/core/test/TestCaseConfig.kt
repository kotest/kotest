package io.kotest.core.test

import io.kotest.core.Tag
import io.kotest.core.config.Project
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
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
   val enabledIf: EnabledIf = { true }
) {
   init {
      require(invocations > 0) { "Number of invocations must be greater than 0" }
      require(threads > 0) { "Number of threads must be greater than 0" }
      require(threads <= invocations) { "Number of threads must be <= number of invocations" }
   }
}

typealias EnabledIf = (TestCase) -> Boolean

/**
 * Creates a [TestCaseConfig] from the given parameters, reverting to the
 * receiver for null parameters.
 */
@OptIn(ExperimentalTime::class)
fun TestCaseConfig.deriveTestConfig(
   enabled: Boolean? = null,
   tags: Set<Tag>? = null,
   extensions: List<TestCaseExtension>? = null,
   timeout: Duration? = null,
   invocationTimeout: Duration? = null,
   enabledIf: EnabledIf? = null,
   invocations: Int? = null,
   threads: Int? = null,
   listeners: List<TestListener>? = null
) = TestCaseConfig(
   enabled = enabled ?: this.enabled,
   tags = tags ?: this.tags,
   extensions = extensions ?: this.extensions,
   listeners = listeners ?: this.listeners,
   timeout = timeout ?: this.timeout,
   invocationTimeout = invocationTimeout ?: this.invocationTimeout,
   enabledIf = enabledIf ?: this.enabledIf,
   invocations = invocations ?: this.invocations,
   threads = threads ?: this.threads
)

/**
 * Returns the timeout for a [TestCase] taking into account project settings.
 */
@OptIn(ExperimentalTime::class)
fun TestCaseConfig.resolvedTimeout(): Duration = this.timeout ?: Project.timeout()

/**
 * Returns the timeout for a test invocation taking into account project settings.
 */
@OptIn(ExperimentalTime::class)
fun TestCaseConfig.resolvedInvocationTimeout(): Duration = this.invocationTimeout ?: Project.invocationTimeout()

fun TestCaseConfig.withXDisabled(xdisabled: Boolean) = if (xdisabled) copy(enabled = false) else this
