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

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DoNotParallelize
