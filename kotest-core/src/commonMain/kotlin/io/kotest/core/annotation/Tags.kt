package io.kotest.core.annotation

/**
 * Attach tag to [io.kotest.core.spec.Spec], excluded spec won't be initiated.
 * An unannotated spec will still be instantiated to order to check if root tests are included.
 */
// @Inherited TODO Not supported by Kotlin yet, better to have it so Tags can be added to base spec
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Tags(vararg val values: String)

/**
 * Attach tag to [io.kotest.core.spec.Spec], and that spec won't be instantiated or executed.
 */
// @Inherited TODO Not supported by Kotlin yet, better to have it so Tags can be added to base spec
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Ignored()
