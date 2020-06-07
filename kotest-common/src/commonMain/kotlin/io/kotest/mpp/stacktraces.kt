package io.kotest.mpp

expect val stacktraces: StackTraces

object BasicStackTraces : StackTraces {
   override fun throwableLocation(t: Throwable): String? = null
   override fun throwableLocation(t: Throwable, n: Int): List<String>? = null
   override fun <T : Throwable> cleanStackTrace(throwable: T): T = throwable
   override fun root(throwable: Throwable): Throwable = throwable
}

interface StackTraces {

   /**
    * Returns the first line of this stack trace, skipping io.kotest if possible.
    * On some platforms the stack trace may not be available and will return null.
    */
   fun throwableLocation(t: Throwable): String?

   /**
    * Returns the first n lines of this stack trace, skipping io.test if possible.
    * On some platforms the stack trace may not be available and will return null.
    */
   fun throwableLocation(t: Throwable, n: Int): List<String>?

   /**
    * Removes io.kotest stack elements from the given throwable if the platform supports stack traces,
    * otherwise returns the exception as is.
    */
   fun <T : Throwable> cleanStackTrace(throwable: T): T

   /**
    * Returns the root cause of the given throwable. If it has no root cause, or the platform does
    * not support causes, this will be returned.
    */
   fun root(throwable: Throwable): Throwable
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
