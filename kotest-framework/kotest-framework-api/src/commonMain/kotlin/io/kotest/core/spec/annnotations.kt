package io.kotest.core.spec

/**
 * Add this annotation to [Listener]s or [Extensions] and they will be registered automatically
 * for all specs.
 */
annotation class AutoScan

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val name: String)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@DslMarker
annotation class KotestDsl

/**
 * Attach tag to [io.kotest.core.spec.Spec] and a spec excluded by a tag expression won't be instantiated.
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

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DoNotParallelize
