package io.kotest.assertions

object Failures {

   /**
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
    *     Failures.shouldRemoveKotestElementsFromStacktrace = false
    * ```
    */
   var shouldRemoveKotestElementsFromStacktrace: Boolean =
      System.getProperty("kotest.failures.stacktrace.clean", "true") == "true"
}

actual fun cleanStackTrace(throwable: Throwable): Throwable {
   if (Failures.shouldRemoveKotestElementsFromStacktrace)
      throwable.stackTrace = UserStackTraceConverter.getUserStacktrace(throwable.stackTrace)
   return throwable
}

/**
 * Creates an [AssertionError] from the given message. If the platform supports nested exceptions, the cause
 * is set to the given [cause]. If the platform supports stack traces, then the stack is cleaned of `io.kotest`
 * lines.
 */
actual fun createAssertionError(message: String, cause: Throwable?): AssertionError {
   val t = AssertionError(message, cause)
   cleanStackTrace(t)
   return t
}
