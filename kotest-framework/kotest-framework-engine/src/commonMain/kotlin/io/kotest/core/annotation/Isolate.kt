package io.kotest.core.annotation

/**
 * When added to a spec, will mark that spec to not run in parallel, regardless
 * of concurrency or parallelism settings.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Isolate
