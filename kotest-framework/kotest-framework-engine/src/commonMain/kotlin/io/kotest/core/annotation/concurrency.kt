package io.kotest.core.annotation

/**
 * When added to a spec, will mark that spec to not run in parallel, regardless
 * of concurrency settings.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Isolate

/**
 * When added to a spec, will mark that spec to always run in parallel, regardless
 * of concurrency settings.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Parallel

/**
 * Use [Isolate].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Deprecated("Use Isolate. Deprecated since 6.0", ReplaceWith("Isolate", "io.kotest.core.annotation.Isolate"))
annotation class DoNotParallelize
