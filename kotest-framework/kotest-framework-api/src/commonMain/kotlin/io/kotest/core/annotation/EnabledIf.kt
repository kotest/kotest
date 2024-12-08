package io.kotest.core.annotation

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Attach to a [io.kotest.core.spec.Spec], and the referenced [EnabledCondition] will be
 * instantiated and the [enabledIf] function invoked.
 *
 * Implementations of [EnabledCondition] must be classes (not objects) and contain a no-arg constructor
 * as they will be created via reflection.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnabledIf(val enabledIf: KClass<out EnabledCondition>)

fun interface EnabledCondition {
   fun enabled(kclass: KClass<out Spec>): Boolean
}

class AlwaysEnabledCondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = true
}

class NeverEnabled : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = false
}
