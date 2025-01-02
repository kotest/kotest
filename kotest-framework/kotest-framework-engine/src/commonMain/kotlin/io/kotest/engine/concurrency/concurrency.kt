@file:Suppress("DEPRECATION")

package io.kotest.engine.concurrency

import io.kotest.core.annotation.DoNotParallelize
import io.kotest.core.annotation.Isolate
import io.kotest.mpp.IncludingAnnotations
import io.kotest.mpp.IncludingSuperclasses
import io.kotest.mpp.hasAnnotation
import kotlin.reflect.KClass

/**
 * Returns true if this class is annotated with either of the annotations used to indicate
 * this spec should not run concurrently regardless of config.
 *
 * Those annotations are [DoNotParallelize] and [Isolate].
 */
internal fun KClass<*>.isIsolate(): Boolean =
   hasAnnotation<DoNotParallelize>(IncludingAnnotations, IncludingSuperclasses)
      || hasAnnotation<Isolate>(IncludingAnnotations, IncludingSuperclasses)

