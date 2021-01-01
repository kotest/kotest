package io.kotest.core.internal

import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

fun KClass<*>.isIsolate(): Boolean = annotation<DoNotParallelize>() != null || annotation<Isolate>() != null

@Deprecated("Explicit thread mode will be removed in 5.0; use parallelism setting with concurrency mode")
fun Spec.resolvedThreads(): Int? = this.threads() ?: this.threads

/**
 * Returns the [ConcurrencyMode] to use for tests in this spec.
 *
 * Note that if this spec is annotated @Isolate then the value will be [ConcurrencyMode.None]
 * regardless of the config setting.
 */
//fun Spec.resolvedConcurrencyMode(): ConcurrencyMode? =
//   if (this::class.isIsolate()) ConcurrencyMode.None else
//      this.concurrencyMode() ?: configuration.concurrencyMode
