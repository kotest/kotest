package io.kotest.engine.concurrency

import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.Isolate
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

/**
 * Returns true if this class is annotated with either of the annotations used to indicate
 * this spec should not run concurrently regardless of config.
 *
 * Those annotations are [DoNotParallelize] and [Isolate].
 */
internal fun KClass<*>.isIsolate(): Boolean = annotation<DoNotParallelize>() != null || annotation<Isolate>() != null

