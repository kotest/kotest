package io.kotest.core.annotation

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Attach to a [io.kotest.core.spec.Spec], and the referenced [Condition] will be
 * instantiated and the [condition] function invoked.
 *
 * Implementations of [Condition] must be classes (not objects) and contain a no-arg constructor
 * as they will be created via reflection.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnabledIf(val condition: KClass<out Condition>)

/**
 * Attach to a [io.kotest.core.spec.Spec], and the referenced [Condition] will be
 * instantiated and the [condition] function invoked. If this function returns true, then
 * then spec will be skipped.
 *
 * Implementations of [EnabledCondition] must be classes (not objects) and contain a no-arg constructor
 * as they will be created via reflection.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisabledIf(val condition: KClass<out Condition>)

@Deprecated("Use Condition instead. Deprecated since 6.0", ReplaceWith("Condition"))
typealias EnabledCondition = Condition

/**
 * A condition that can be used to enable or disable a spec.
 *
 * Implementations of [Condition] must be classes (not objects) and contain a no-arg constructor
 * as they will be created via reflection.
 */
fun interface Condition {
   fun evaluate(kclass: KClass<out Spec>): Boolean
}

class AlwaysTrueCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = true
}

class AlwaysFalseCondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = false
}
