package io.kotest.mpp

expect object StackTraces {

   /**
    * Returns the first line of this stack trace, skipping io.kotest if possible.
    * On some platforms the stack trace may not be available.
    */
   fun Throwable.throwableLocation(): String?

   /**
    * Removes io.kotest stack elements from the given throwable if the platform supports stack traces,
    * otherwise returns the exception as is.
    */
   fun <T : Throwable> cleanStackTrace(throwable: T): T
}

/**
 * JVM supported only.
 *
 * Whether Kotest-related frames will be removed from the stack traces of thrown [AssertionError]s.
 *
 * This defaults to `true`. You can change it by setting the system property `kotest.failures.stacktrace.clean`
 * or at runtime, by reassigning this var.
 *
 * E.g.:
 *
 * ```
 *     -Dkotest.failures.stacktrace.clean=false
 * ```
 *
 * or
 *
 * ```
 *     StackTraces.shouldRemoveKotestElementsFromStacktrace = false
 * ```
 */
var shouldRemoveKotestElementsFromStacktrace: Boolean = sysprop("kotest.failures.stacktrace.clean", "true") == "true"
