package io.kotest.core.internal

import io.kotest.core.config.LaunchMode
import io.kotest.core.config.configuration
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

fun KClass<*>.isIsolate(): Boolean = annotation<DoNotParallelize>() != null || annotation<Isolate>() != null

@Deprecated("Explicit thread mode will be removed in 5.0; use parallelism setting with concurrency mode")
fun Spec.resolvedThreads(): Int? = this.threads() ?: this.threads

/**
 * Returns the [LaunchMode] to use for tests in this spec.
 *
 * Note that if this spec is annotated with @Isolate then the value will be [LaunchMode.Consecutive]
 * regardless of the config setting.
 */
fun Spec.resolvedTestLaunchMode(): LaunchMode? =
   if (this::class.isIsolate()) LaunchMode.Consecutive else
      this.launchMode ?: configuration.testLaunchMode
