package io.kotest.core.annotation

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Attach to a [io.kotest.core.spec.Spec] and a spec excluded by a tag expression
 * won't be instantiated. An unannotated spec will still need be instantiated to
 * order to examine any tags registered by the constructors.
 */
// @Inherited TODO Not supported by Kotlin yet
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Tags(vararg val values: String)

/**
 * Attach to a [io.kotest.core.spec.Spec], and that spec won't be instantiated if the tag(s) are not
 * provided at runtime. If more than one tag is specified then all tags must be provided at runtime.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresTag(vararg val values: String)

/**
 * Attach to a [io.kotest.core.spec.Spec], and that spec won't be instantiated or executed.
 */
// @Inherited TODO Not supported by Kotlin yet
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Ignored

/**
 * Attach to a [io.kotest.core.spec.Spec], and the referenced [EnabledCondition] will be
 * instantiated and the [enabledIf] function invoked.
 *
 * Implementations must contain a no-arg constructor as it will be created via reflection.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnabledIf(val enabledIf: KClass<out EnabledCondition>)

fun interface EnabledCondition {
    fun enabled(kclass: KClass<out Spec>): Boolean
}
