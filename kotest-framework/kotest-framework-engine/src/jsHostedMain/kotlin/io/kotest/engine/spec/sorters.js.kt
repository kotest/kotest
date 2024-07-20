package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Note: No file system so this sort order is a no-op
 */
actual val FailureFirstSorter: SpecSorter = object : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = 0
}

/**
 * Note: Runtime annotations are not supported on Native or JS so on those platforms
 * this sort order is a no-op.
 */
actual val AnnotatedSpecSorter: SpecSorter = object : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = 0
}
