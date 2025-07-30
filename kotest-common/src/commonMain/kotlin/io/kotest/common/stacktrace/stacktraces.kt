package io.kotest.common.stacktrace

expect val stacktraces: StackTraces

interface StackTraces {

   /**
    * Returns the first line of this stack trace, skipping io.kotest if possible.
    * On some platforms the stack trace may not be available and will return null.
    */
   fun throwableLocation(t: Throwable): String? = throwableLocation(t, 1)?.firstOrNull()

   /**
    * Returns the first n lines of this stack trace, skipping io.kotest if possible.
    * On some platforms the stack trace may not be available and will return null.
    */
   fun throwableLocation(t: Throwable, n: Int): List<String>?

   /**
    * Removes io.kotest stack elements from the given throwable if the platform supports stack traces,
    * otherwise returns the exception as is.
    */
   fun <T : Throwable> cleanStackTrace(t: T): T

   /**
    * Returns the root cause of the given throwable. If it has no root cause, or the platform does
    * not support causes, this will be returned.
    */
   fun root(t: Throwable): Throwable
}
