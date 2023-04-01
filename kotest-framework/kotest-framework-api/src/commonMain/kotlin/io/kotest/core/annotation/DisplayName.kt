package io.kotest.core.annotation

/**
 * Note: This name must be globally unique. Two specs, even in different packages,
 * cannot share the same name, so if `@DisplayName` is used, developers must ensure it does not
 * clash with another spec.
 *
 * This annotation only works on JVM targets. On other targets this annotation will be ignored.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val name: String)
