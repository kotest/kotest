package io.kotest.engine.concurrency

import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.engine.spec.isIsolate
import kotlin.math.max

/**
 * Returns the number of threads specified on this spec, which comes either from the
 * function overrides of the var overrides.
 */
@Deprecated("Setting explicit thread count in a spec has been deprecated. Use the concurrency setting")
internal fun Spec.resolvedThreads(): Int? = this.threads() ?: this.threads

/**
 * Returns the concurrent tests count to use for tests in this spec.
 *
 * If threads is specified on the spec, then that will implicitly raise the concurrentTests
 * count to the same value if concurrentTests is not specified.
 *
 * Note that if this spec is annotated with @Isolate then the value
 * will be 1 regardless of the config setting.
 *
 * spec.concurrency ?: configuration.concurrentTests
 */
internal fun Spec.resolvedConcurrentTests(): Int {
   val fromSpecConcurrency = this.concurrency ?: this.concurrency()
   val fromSpecThreadCount = this.resolvedThreads()
   return when {
      this::class.isIsolate() -> Configuration.Sequential
      fromSpecConcurrency != null -> max(1, fromSpecConcurrency)
      fromSpecThreadCount != null -> max(1, fromSpecThreadCount)
      else -> configuration.concurrentTests
   }
}
