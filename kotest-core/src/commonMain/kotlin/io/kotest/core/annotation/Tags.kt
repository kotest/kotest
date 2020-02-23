package io.kotest.core.annotation

/**
 * Attach tag to [io.kotest.core.spec.Spec], excluded spec won't be initiated.
 */
// @Inherited TODO Not supported by Kotlin yet, better to have it so Tags can be added to base spec
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Tags(vararg val values: String)
