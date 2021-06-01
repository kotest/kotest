package io.kotest.core.spec

/**
 * Add this annotation to [Listener]s or [Extensions] and they will be registered automatically
 * for all specs.
 */
annotation class AutoScan

/**
 * Note: This name must be globally unique. Two specs, even in different packages,
 * cannot share the same name, so if @DisplayName is used, developers must ensure it does not
 * clash with another spec.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val name: String)

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
@DslMarker
annotation class KotestDsl

/**
 * Use [Isolate].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DoNotParallelize

/**
 * When added to a spec, will mark that spec to not run in parallel, regardless
 * of concurrency or parallelism settings.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Isolate
