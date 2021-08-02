package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

actual class FailureFirstSorter : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = 0
}

actual object AnnotatedSpecSorter : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = 0
}
