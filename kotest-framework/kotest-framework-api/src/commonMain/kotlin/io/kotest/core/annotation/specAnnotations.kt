package io.kotest.core.annotation

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Attach tag to [io.kotest.core.spec.Spec] and a spec excluded by a tag expression won't be instantiated.
 * An unannotated spec will still be instantiated to order to check if root tests are included.
 */
// @Inherited TODO Not supported by Kotlin yet, better to have it so Tags can be added to base spec
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
 * Attach tag to [io.kotest.core.spec.Spec], and that spec won't be instantiated or executed.
 */
// @Inherited TODO Not supported by Kotlin yet, better to have it so Tags can be added to base spec
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Ignored

/**
 * Attach to [io.kotest.core.spec.Spec], and the logic inside [enabledIf] will be executed
 * to define if a Spec will be instantiated or executed.
 *
 * Implementations must contain a no-arg constructor as it will be created via reflection.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnabledIf(val enabledIf: KClass<out EnabledCondition>)

fun interface EnabledCondition {
    fun enabled(specKlass: KClass<out Spec>): Boolean
}
