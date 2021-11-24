package io.kotest.core.annotation

/**
 * Use [Isolate].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DoNotParallelize
