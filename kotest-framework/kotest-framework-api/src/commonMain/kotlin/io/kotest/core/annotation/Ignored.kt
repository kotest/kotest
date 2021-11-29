package io.kotest.core.annotation

/**
 * Attach to a [io.kotest.core.spec.Spec], and that spec won't be instantiated or executed.
 */
// @Inherited TODO Not supported by Kotlin yet
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Ignored
