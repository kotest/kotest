package io.kotest.core.spec.style

/**
 * Annotates a function as a runnable test.
 *
 * The function must have at least one parameter, where the first parameter is a String, which is
 * used when generating the test path to execute.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TestRunnable
