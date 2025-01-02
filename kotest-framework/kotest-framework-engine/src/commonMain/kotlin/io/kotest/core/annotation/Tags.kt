package io.kotest.core.annotation

/**
 * Attach to a [io.kotest.core.spec.Spec] and a spec excluded by a tag expression
 * won't be instantiated. An unannotated spec will still need be instantiated to
 * order to examine any tags registered by the constructors.
 */
// @Inherited TODO Not supported by Kotlin yet
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Tags(vararg val values: String)

