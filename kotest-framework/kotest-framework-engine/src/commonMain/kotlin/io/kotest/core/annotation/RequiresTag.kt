package io.kotest.core.annotation

/**
 * Attach to a [io.kotest.core.spec.Spec], and that spec won't be instantiated if the tag(s) are not
 * provided at runtime. If more than one tag is specified then all tags must be provided at runtime.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresTag(vararg val values: String)
