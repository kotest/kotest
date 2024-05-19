package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

actual val FailureFirstSorter: SpecSorter = object : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = 0
}

actual val AnnotatedSpecSorter: SpecSorter = object : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = 0
}
