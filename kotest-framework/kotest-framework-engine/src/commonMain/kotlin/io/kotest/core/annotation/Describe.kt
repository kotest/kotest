package io.kotest.core.annotation

/**
 * Attach this annotation to a spec class to add human-readable details on what that spec is testing.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Description(val value: String)

/**
 * Attach this annotation to add a link to an external issue tracker.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Issue(val value: String)
