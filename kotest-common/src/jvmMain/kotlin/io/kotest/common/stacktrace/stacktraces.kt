@file:JvmName("stacktracesjvm")

package io.kotest.common.stacktrace

import io.kotest.common.JVMOnly
import io.kotest.common.sysprop

actual val stacktraces: StackTraces = JvmStacktraces

object JvmStacktraces : StackTraces {

   override fun throwableLocation(t: Throwable, n: Int): List<String> {
      // stackTrace can error if the throwable is mocked
      return try {
         (t.cause ?: t).stackTrace?.dropWhile {
            it.className.startsWith("io.kotest")
         }?.take(n)?.map { it.toString() } ?: emptyList()
      } catch (_: Throwable) {
         emptyList()
      }
   }

   override fun <T : Throwable> cleanStackTrace(t: T): T {
      if (shouldRemoveKotestElementsFromStacktrace) {
         // stackTrace can error if the throwable is mocked
         try {
            t.stackTrace = UserStackTraceConverter.getUserStacktrace(t.stackTrace)
         } catch (_: Throwable) {
         }
      }
      return t
   }

   override fun root(t: Throwable): Throwable {
      // cause can error if the throwable is mocked
      return try {
         val cause = t.cause
         if (cause == null) t else root(cause)
      } catch (_: Throwable) {
         t
      }
   }
}

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
 *     StackTraces.shouldRemoveKotestElementsFromStacktrace = false
 * ```
 */
@JVMOnly
var shouldRemoveKotestElementsFromStacktrace: Boolean = sysprop("kotest.failures.stacktrace.clean", "true") == "true"
