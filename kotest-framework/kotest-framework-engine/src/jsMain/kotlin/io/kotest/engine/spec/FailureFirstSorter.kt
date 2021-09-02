package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

actual class FailureFirstSorter : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = 0
}

/**
 * An implementation of [SpecExecutionOrder] which will order specs based on the integer
 * value of the [Order] annotation. If the annotation is not present, then that spec is
 * assumed to have a [Int.MAX_VALUE] default value.
 *
 * Note: Runtime annotations are not supported on Native or JS so on those platforms
 * this sort order is a no-op.
 */
actual object AnnotatedSpecSorter : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = 0
}
