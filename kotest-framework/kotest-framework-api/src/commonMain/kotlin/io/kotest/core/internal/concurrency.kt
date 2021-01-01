package io.kotest.core.internal

import io.kotest.core.config.LaunchMode
import io.kotest.core.config.configuration
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

/**
 * Returns true if this class is annotated with either of the annotations used to indicate
 * this spec should not run concurrently regardless of config.
 */
fun KClass<*>.isIsolate(): Boolean = annotation<DoNotParallelize>() != null || annotation<Isolate>() != null

/**
 * Returns the number of threads specified on this spec, which comes either from the
 * function overrides of the var overrides.
 */
fun Spec.resolvedThreads(): Int? = this.threads() ?: this.threads

/**
 * Returns the explicit dispatcher set for tests in this spec, which comes either from the
 * function overrides of the var overrides.
 */
fun Spec.resolvedDispatcher() = this.dispatcher() ?: this.dispatcher

/**
 * Returns the [LaunchMode] to use for tests in this spec.
 *
 * Note that if this spec is annotated with @Isolate then the value
 * will be [LaunchMode.Consecutive] regardless of the config setting.
 *
 * If the spec specifies a thread count greater than 1, then this will
 * implicitly activate [LaunchMode.Concurrent].
 */
fun Spec.resolvedTestLaunchMode(): LaunchMode? = when {
   this::class.isIsolate() -> LaunchMode.Consecutive
   this.resolvedThreads() ?: 0 > 1 -> LaunchMode.Concurrent
   else -> this.launchMode ?: configuration.testLaunchMode
}
